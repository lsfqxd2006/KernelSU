plugins {
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.compose.compiler) apply false
}

val androidMinSdkVersion by extra(31)
val androidTargetSdkVersion by extra(37)
val androidCompileSdkVersion by extra(37)
val androidCompileSdkVersionMinor by extra(0)
val androidBuildToolsVersion by extra("37.0.0")
val androidCompileNdkVersion: String by extra(libs.versions.ndk.get())
val androidSourceCompatibility by extra(JavaVersion.VERSION_21)
val androidTargetCompatibility by extra(JavaVersion.VERSION_21)
val managerVersionCode by extra(getVersionCode())
val managerVersionName by extra(getVersionName())

// 固定上游仓库地址
val upstreamRepo = "https://github.com/tiann/KernelSU.git"

// 获取上游 main 分支总提交数（用于 versionCode）
fun getGitCommitCount(): Int {
    return try {
        // 拉取上游最新提交信息
        Runtime.getRuntime().exec(arrayOf("git", "fetch", upstreamRepo, "main")).waitFor()
        // 统计上游 main 分支提交总数
        val process = Runtime.getRuntime().exec(arrayOf("git", "rev-list", "--count", "FETCH_HEAD"))
        process.inputStream.bufferedReader().use { it.readText().trim().toInt() }
    } catch (e: Exception) {
        0
    }
}

// 获取上游最新 tag（用于 versionName）
fun getGitDescribe(): String {
    return try {
        // 拉取上游所有 tag
        Runtime.getRuntime().exec(arrayOf("git", "fetch", upstreamRepo, "--tags")).waitFor()
        // 显示上游最新 tag
        val process = Runtime.getRuntime().exec(arrayOf("git", "describe", "--tags", "--always", "FETCH_HEAD"))
        process.inputStream.bufferedReader().use { it.readText().trim() }
    } catch (e: Exception) {
        "unknown"
    }
}

// 版本号 = 官方基数 + 官方提交数
fun getVersionCode(): Int {
    val commitCount = getGitCommitCount()
    return 30000 + commitCount
}

// 版本名 = 官方最新 tag
fun getVersionName(): String {
    return getGitDescribe()
}
