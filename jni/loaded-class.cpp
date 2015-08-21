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

extern "C"{

    enum DexClassVerifyMode {
         VERIFY_MODE_UNKNOWN = 0,
         VERIFY_MODE_NONE,
         VERIFY_MODE_REMOTE,
         VERIFY_MODE_ALL
    };

    enum ExecutionMode {
         kExecutionModeUnknown = 0,
         kExecutionModeInterpPortable,
         kExecutionModeInterpFast,
         #if defined(WITH_JIT)
            kExecutionModeJit,
         #endif
    };

    enum JdwpTransportType {
        kJdwpTransportUnknown = 0,
        kJdwpTransportSocket,       /* transport=dt_socket */
        kJdwpTransportAndroidAdb,   /* transport=dt_android_adb */
    };

    enum ProfilerClockSource {
        kProfilerClockSourceThreadCpu,
        kProfilerClockSourceWall,
        kProfilerClockSourceDual,
    };

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

    enum DexOptimizerMode {
        OPTIMIZE_MODE_UNKNOWN = 0,
        OPTIMIZE_MODE_NONE,         /* never optimize (except "essential") */
        OPTIMIZE_MODE_VERIFIED,     /* only optimize verified classes (default) */
        OPTIMIZE_MODE_ALL,          /* optimize verified & unverified (risky) */
        OPTIMIZE_MODE_FULL          /* fully opt verified classes at load time */
    };

    enum RegisterMapMode {
        kRegisterMapModeUnknown = 0,
        kRegisterMapModeTypePrecise,
        kRegisterMapModeLivePrecise
    };

    struct AssertionControl {
        char*   pkgOrClass;         /* package/class string, or NULL for esa/dsa */
        int     pkgOrClassLen;      /* string length, for quick compare */
        bool    enable;             /* enable or disable */
        bool    isPackage;          /* string ended with "..."? */
    };

    typedef uint32_t            u4;

    typedef void (*HashFreeFunc)(void* ptr);

    struct HashEntry {
        u4 hashValue;
        void* data;
    };

    struct HashTable {
        int         tableSize;          /* must be power of 2 */
        int         numEntries;         /* current #of "live" entries */
        int         numDeadEntries;     /* current #of tombstone entries */
        HashEntry*  pEntries;           /* array on heap */
        HashFreeFunc freeFunc;
        pthread_mutex_t lock;
    };

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

    DvmGlobals *gDvm;
    void* so;

    int load_gDvm(){
        if(NULL==gDvm){
            __android_log_print(ANDROID_LOG_ERROR, "Xposed", "load_gDvm");
            so = dlopen("/system/lib/libdvm.so", 0);
            void* gDvm_addr = dlsym(so, "gDvm");
            gDvm = (DvmGlobals*)gDvm_addr;
            __android_log_print(ANDROID_LOG_ERROR, "Xposed", "got it");
        }
        return 0;
    }

    jstring Java_com_andr0day_appinfo_common_ClassUtil_stringFromJNI( JNIEnv* env,jobject thiz ){
        void* so = dlopen("/system/lib/libdvm.so", RTLD_NOW|RTLD_GLOBAL);
        __android_log_print(ANDROID_LOG_ERROR, "Xposed", "instance:%p", so);
        void* gDvm_addr = dlsym(so, "gDvmJni");
        __android_log_print(ANDROID_LOG_ERROR, "Xposed", "gDvm_addr:%p", gDvm_addr);
        return env->NewStringUTF("string from jni ");
    }

    ClassObject* Java_com_andr0day_appinfo_common_ClassUtil_getLoadedClass(JNIEnv* env, jobject thiz, jint index){
        load_gDvm();
        HashTable *hash_table = gDvm->loadedClasses;
        HashEntry* pEntry;
        pEntry = &hash_table->pEntries[index];
        ClassObject* clazz =  (ClassObject*)pEntry->data;
        return clazz;
    }

    jint Java_com_andr0day_appinfo_common_ClassUtil_getTableSize(JNIEnv* env, jobject thiz){
        load_gDvm();
        HashTable *hash_table = gDvm->loadedClasses;
        int table_size = hash_table->tableSize;
        return table_size;
    }

    jint Java_com_andr0day_appinfo_common_ClassUtil_close(JNIEnv* env, jobject thiz){
        if(NULL!=so){
            dlclose(so);
        }
    }

}
