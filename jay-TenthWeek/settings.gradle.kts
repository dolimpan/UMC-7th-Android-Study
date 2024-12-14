pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*") // Android 관련 그룹
                includeGroupByRegex("com\\.google.*") // Google 관련 그룹
                includeGroupByRegex("androidx.*") // AndroidX 관련 그룹
            }
        }

        mavenCentral() // Maven Central 저장소
        gradlePluginPortal() // Gradle 플러그인 포털
        maven {
            url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") // Kakao Maven 저장소
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Google Maven 저장소
        mavenCentral() // Maven Central 저장소
        maven {
            url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") // Kakao Maven 저장소
        }
    }
}

rootProject.name = "SL"
include(":app")
