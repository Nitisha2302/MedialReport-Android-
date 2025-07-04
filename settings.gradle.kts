pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven {
//            url = uri("http://raw.github.com/saki4510t/libcommon/master/repository/")
            url = uri("http://maven.aliyun.com/nexus/content/groups/public/")
            isAllowInsecureProtocol = true

        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven {
//            url = uri("http://raw.github.com/saki4510t/libcommon/master/repository/")
            url = uri("http://maven.aliyun.com/nexus/content/groups/public/")
            isAllowInsecureProtocol = true
        }
        flatDir {
            dirs("../aars")
        }
    }
}

rootProject.name = "MedicalReport"
include(":app")
include(":libusbcamera",":my-local-sdk")

 