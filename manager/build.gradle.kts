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

fun getGitCommitCount(): Int {
    return try {
        // 修复：单次拉取，不污染本地，官方上游 main
        val process = Runtime.getRuntime().exec(arrayOf("git", "ls-remote", "--refs", "https://github.com/tiann/KernelSU.git", "heads/main"))
        val headHash = process.inputStream.bufferedReader().use { it.readText().trim().split("\t")[0] }
        
        // 直接计算远程提交数，不修改本地git
        val countProcess = Runtime.getRuntime().exec(arrayOf("git", "rev-list", "--count", headHash))
        countProcess.inputStream.bufferedReader().use { it.readText().trim().toInt() }
    } catch (e: Exception) {
        0
    }
}

fun getGitDescribe(): String {
    return try {
        // 修复：直接获取远程最新tag，不拉取到本地，无冲突
        val process = Runtime.getRuntime().exec(arrayOf("git", "ls-remote", "--tags", "--sort=v:refname", "https://github.com/tiann/KernelSU.git"))
        val tagLine = process.inputStream.bufferedReader().useLines { it.lastOrNull() }
        tagLine?.split("/")?.last() ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }
}

fun getVersionCode(): Int {
    val commitCount = getGitCommitCount()
    return 30000 + commitCount
}

fun getVersionName(): String {
    return getGitDescribe()
}
