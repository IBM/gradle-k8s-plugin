package com.ibm.gradle.plugin.kubernetes.extension.common

enum VolumeFrom {

    SECRET("secret"),
    CONFIG_MAP("configMap")

    String value;

    VolumeFrom(String value) {
        this.value = value;
    }
}
