java_library(
    name = "everything",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@com_github_jponge_lzma_java//jar",
        "@com_github_danny02_AnnotationsProcessing//jar",
    ],
    plugins = [
        ":service_loader"
    ]
)

java_plugin(
    name = "service_loader",
    processor_class = "darwin.annotations.ServiceProcessor",
    deps = [
        "@com_github_danny02_AnnotationsProcessing//jar"
    ]
)

java_test(
    name = "tests",
    size = "small",
    test_class = "darwin.jopenctm.CtmTests",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**"]),
    deps = [
        ":everything",
        "@junit_junit//jar",
    ],
)