package com.ibm.gradle.plugin.kubernetes.extension.common

enum ValueFrom {
    SECRET("secret"),
    CONFIG_MAP("configMap")

    String value;

    ValueFrom(String value) {
        this.value = value;
    }
}
