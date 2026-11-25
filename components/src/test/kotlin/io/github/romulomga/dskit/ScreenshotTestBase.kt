package io.github.romulomga.dskit

import org.robolectric.annotation.Config

/**
 * Base dos testes de screenshot do DSKit: a do `screenshot-testing` (Robolectric com renderização nativa, sem
 * emulador, SDK 35), numa tela alta para caber a moldura de cada componente.
 */
@Config(qualifiers = "w400dp-h2000dp-xhdpi")
abstract class ScreenshotTestBase : DSScreenshotTest()
