object Configs {
    object Common {
        const val compileSdk = 37
        const val minSdk = 26
        const val targetSdk = 36
        const val javaVersion = 21
    }

    object Modules {
        object Tokens {
            const val namespace = "io.github.romulomga.dskit.tokens"
            const val name = ":tokens"
        }

        object Components {
            const val namespace = "io.github.romulomga.dskit.components"
            const val name = ":components"
        }

        object Lint {
            const val name = ":lint"
        }

        object Sample {
            const val namespace = "io.github.romulomga.dskit.sample"
            const val applicationId = "io.github.romulomga.dskit.sample"
            const val name = ":sample"
        }
    }
}
