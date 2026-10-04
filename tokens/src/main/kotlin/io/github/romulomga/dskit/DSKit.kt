package io.github.romulomga.dskit

import io.github.romulomga.dskit.tokens.BuildConfig

/** Identidade da lib. A versão é a mesma da tag do git (o teste `DSKitVersionTest` confere). */
public object DSKit {
    public const val VERSION: String = BuildConfig.DSKIT_VERSION
}
