/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <stdint.h>
#include <pthread.h>

#include <string>
#include <vector>

#include <stdarg.h>

#include "DexFile.h"

#define RETURN_PTR(_val)        do { pResult->l = (Object*)(_val); return; } while(0)

/*
 * Global verification mode.  These must be in order from least verification
 * to most.  If we're using "exact GC", we may need to perform some of
 * the verification steps anyway.
 */
enum DexClassVerifyMode {
     VERIFY_MODE_UNKNOWN = 0,
     VERIFY_MODE_NONE,
     VERIFY_MODE_REMOTE,
     VERIFY_MODE_ALL
};

/*
  * Execution mode, e.g. interpreter vs. JIT.
  */
 enum ExecutionMode {
     kExecutionModeUnknown = 0,
     kExecutionModeInterpPortable,
     kExecutionModeInterpFast,
     #if defined(WITH_JIT)
        kExecutionModeJit,
     #endif
 };

/*
 * How we talk to the debugger.
 */
enum JdwpTransportType {
    kJdwpTransportUnknown = 0,
    kJdwpTransportSocket,       /* transport=dt_socket */
    kJdwpTransportAndroidAdb,   /* transport=dt_android_adb */
};

/*
 * Profiler clock source.  keep
 */
enum ProfilerClockSource {
    kProfilerClockSourceThreadCpu,
    kProfilerClockSourceWall,
    kProfilerClockSourceDual,
};

/*
 * The classpath and bootclasspath differ in that only the latter is
 * consulted when looking for classes needed by the VM.  When searching
 * for an arbitrary class definition, we start with the bootclasspath,
 * look for optional packages (a/k/a standard extensions), and then try
 * the classpath.
 *
 * In Dalvik, a class can be found in one of two ways:
 *  - in a .dex file
 *  - in a .dex file named specifically "classes.dex", which is held
 *    inside a jar file
 *
 * These two may be freely intermixed in a classpath specification.
 * Ordering is significant.
 */
enum ClassPathEntryKind {
    kCpeUnknown = 0,
    kCpeJar,
    kCpeDex,
    kCpeLastEntry       /* used as sentinel at end of array */
};


struct ClassPathEntry {
    ClassPathEntryKind kind;
    char*   fileName;
    void*   ptr;            /* JarFile* or DexFile* */
};

/*
 * Global DEX optimizer control.  Determines the circumstances in which we
 * try to rewrite instructions in the DEX file.
 *
 * Optimizing is performed ahead-of-time by dexopt and, in some cases, at
 * load time by the VM.
 */
enum DexOptimizerMode {
    OPTIMIZE_MODE_UNKNOWN = 0,
    OPTIMIZE_MODE_NONE,         /* never optimize (except "essential") */
    OPTIMIZE_MODE_VERIFIED,     /* only optimize verified classes (default) */
    OPTIMIZE_MODE_ALL,          /* optimize verified & unverified (risky) */
    OPTIMIZE_MODE_FULL          /* fully opt verified classes at load time */
};

/*
 * Register map generation mode.  Only applicable when generateRegisterMaps
 * is enabled.  (The "disabled" state is not folded into this because
 * there are callers like dexopt that want to enable/disable without
 * specifying the configuration details.)
 *
 * "TypePrecise" is slower and requires additional storage for the register
 * maps, but allows type-precise GC.  "LivePrecise" is even slower and
 * requires additional heap during processing, but allows live-precise GC.
 */
enum RegisterMapMode {
    kRegisterMapModeUnknown = 0,
    kRegisterMapModeTypePrecise,
    kRegisterMapModeLivePrecise
};


/*
 * One of these for each -ea/-da/-esa/-dsa on the command line.
 */
struct AssertionControl {
    char*   pkgOrClass;         /* package/class string, or NULL for esa/dsa */
    int     pkgOrClassLen;      /* string length, for quick compare */
    bool    enable;             /* enable or disable */
    bool    isPackage;          /* string ended with "..."? */
};



typedef uint32_t            u4;


/*
 * This function will be used to free entries in the table.  This can be
 * NULL if no free is required, free(), or a custom function.
 */
typedef void (*HashFreeFunc)(void* ptr);

/*
 * One entry in the hash table.  "data" values are expected to be (or have
 * the same characteristics as) valid pointers.  In particular, a NULL
 * value for "data" indicates an empty slot, and HASH_TOMBSTONE indicates
 * a no-longer-used slot that must be stepped over during probing.
 *
 * Attempting to add a NULL or tombstone value is an error.
 *
 * When an entry is released, we will call (HashFreeFunc)(entry->data).
 */
struct HashEntry {
    u4 hashValue;
    void* data;
};

/*
 * Expandable hash table.
 *
 * This structure should be considered opaque.
 */
struct HashTable {
    int         tableSize;          /* must be power of 2 */
    int         numEntries;         /* current #of "live" entries */
    int         numDeadEntries;     /* current #of tombstone entries */
    HashEntry*  pEntries;           /* array on heap */
    HashFreeFunc freeFunc;
    pthread_mutex_t lock;
};

/*
 * Some additional VM data structures that are associated with the DEX file.
 */
struct DvmDex {
    /* pointer to the DexFile we're associated with */
    DexFile*            pDexFile;

    /* clone of pDexFile->pHeader (it's used frequently enough) */
    const DexHeader*    pHeader;

    /* interned strings; parallel to "stringIds" */
    struct StringObject** pResStrings;

    /* resolved classes; parallel to "typeIds" */
    struct ClassObject** pResClasses;

    /* resolved methods; parallel to "methodIds" */
    struct Method**     pResMethods;

    /* resolved instance fields; parallel to "fieldIds" */
    /* (this holds both InstField and StaticField) */
    struct Field**      pResFields;

    /* interface method lookup cache */
    struct AtomicCache* pInterfaceCache;

    /* shared memory region with file contents */
    bool                isMappedReadOnly;
    MemMapping          memMap;

    jobject dex_object;

    /* lock ensuring mutual exclusion during updates */
    pthread_mutex_t     modLock;
};

struct DvmGlobals {

        /*
         * Some options from the command line or environment.
         */
        char*       bootClassPathStr;
        char*       classPathStr;

        size_t      heapStartingSize;
        size_t      heapMaximumSize;
        size_t      heapGrowthLimit;
        bool        lowMemoryMode;
        double      heapTargetUtilization;
        size_t      heapMinFree;
        size_t      heapMaxFree;
        size_t      stackSize;
        size_t      mainThreadStackSize;

        bool        verboseGc;
        bool        verboseJni;
        bool        verboseClass;
        bool        verboseShutdown;

        bool        jdwpAllowed;        // debugging allowed for this process?
        bool        jdwpConfigured;     // has debugging info been provided?
        JdwpTransportType jdwpTransport;
        bool        jdwpServer;
        char*       jdwpHost;
        int         jdwpPort;
        bool        jdwpSuspend;

        ProfilerClockSource profilerClockSource;

        /*
         * Lock profiling threshold value in milliseconds.  Acquires that
         * exceed threshold are logged.  Acquires within the threshold are
         * logged with a probability of $\frac{time}{threshold}$ .  If the
         * threshold is unset no additional logging occurs.
         */
        u4          lockProfThreshold;

        int         (*vfprintfHook)(FILE*, const char*, va_list);
        void        (*exitHook)(int);
        void        (*abortHook)(void);
        bool        (*isSensitiveThreadHook)(void);

        char*       jniTrace;
        bool        reduceSignals;
        bool        noQuitHandler;
        bool        verifyDexChecksum;
        char*       stackTraceFile;     // for SIGQUIT-inspired output

        bool        logStdio;

        DexOptimizerMode    dexOptMode;
        DexClassVerifyMode  classVerifyMode;

        bool        generateRegisterMaps;
        RegisterMapMode     registerMapMode;

        bool        monitorVerification;

        bool        dexOptForSmp;

        /*
         * GC option flags.
         */
        bool        preciseGc;
        bool        preVerify;
        bool        postVerify;
        bool        concurrentMarkSweep;
        bool        verifyCardTable;
        bool        disableExplicitGc;

        int         assertionCtrlCount;
        AssertionControl*   assertionCtrl;

        ExecutionMode   executionMode;

        bool        commonInit; /* whether common stubs are generated */
        bool        constInit; /* whether global constants are initialized */

        /*
         * VM init management.
         */
        bool        initializing;
        bool        optimizing;

        /*
         * java.lang.System properties set from the command line with -D.
         * This is effectively a set, where later entries override earlier
         * ones.
         */
        std::vector<std::string>* properties;

        /*
         * Where the VM goes to find system classes.
         */
        ClassPathEntry* bootClassPath;
        /* used by the DEX optimizer to load classes from an unfinished DEX */
        DvmDex*     bootClassPathOptExtra;
        bool        optimizingBootstrapClass;

        /*
         * Loaded classes, hashed by class name.  Each entry is a ClassObject*,
         * allocated in GC space.
         */
        HashTable*  loadedClasses;
};




/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
extern "C" jstring
Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
#if defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
   #define ABI "x86"
#elif defined(__x86_64__)
   #define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
   #define ABI "mips64"
#elif defined(__mips__)
   #define ABI "mips"
#elif defined(__aarch64__)
   #define ABI "arm64-v8a"
#else
   #define ABI "unknown"
#endif

    void* so = dlopen("/system/lib/libdvm.so", 0);
    __android_log_print(ANDROID_LOG_ERROR, "JNIMsg", "instance:%p", so);
    void* gDvm_addr = dlsym(so, "gDvm");
    DvmGlobals *gDvm = (DvmGlobals*)gDvm_addr;

    HashTable *hash_table = gDvm->loadedClasses;

    __android_log_print(ANDROID_LOG_ERROR, "JNIMsg", "gDvm_addr:%p", gDvm_addr);
    __android_log_print(ANDROID_LOG_ERROR, "JNIMsg", "bootClassPathStr:%s", gDvm->bootClassPathStr);
    __android_log_print(ANDROID_LOG_ERROR, "JNIMsg", "classPathStr:%s", gDvm->classPathStr);

    __android_log_print(ANDROID_LOG_ERROR, "JNIMsg", "loadedClasses:%p", hash_table);
    __android_log_print(ANDROID_LOG_ERROR, "JNIMsg", "table-size:%d", hash_table->tableSize);
    __android_log_print(ANDROID_LOG_ERROR, "JNIMsg", "numEntries:%d", hash_table->numEntries);

    return env->NewStringUTF("4 " ABI ".");
}


extern "C" ClassObject*
Java_com_example_hellojni_HelloJni_getLoadedClass(JNIEnv* env, jobject thiz, jint index){
   void* so = dlopen("/system/lib/libdvm.so", 0);
   void* gDvm_addr = dlsym(so, "gDvm");
   DvmGlobals *gDvm = (DvmGlobals*)gDvm_addr;
   HashTable *hash_table = gDvm->loadedClasses;
   int table_size = hash_table->tableSize;
   HashEntry* pEntry;
   pEntry = &hash_table->pEntries[index];
   ClassObject* clazz =  (ClassObject*)pEntry->data;
   return clazz;
}

extern "C" jint
Java_com_example_hellojni_HelloJni_getTableSize(JNIEnv* env, jobject thiz){
   void* so = dlopen("/system/lib/libdvm.so", 0);
   void* gDvm_addr = dlsym(so, "gDvm");
   DvmGlobals *gDvm = (DvmGlobals*)gDvm_addr;
   HashTable *hash_table = gDvm->loadedClasses;
   int table_size = hash_table->tableSize;
   return table_size;
}
