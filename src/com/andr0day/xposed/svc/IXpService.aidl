package com.andr0day.xposed.svc;

interface IXpService {

    List<String> getClassLoaders();

    List<String> getClasses();

    List<String> getMethods(String className);

}
