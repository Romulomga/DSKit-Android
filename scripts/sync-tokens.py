#!/usr/bin/env python3
"""Gera tokens/.../DSColorTokens.kt a partir dos colorsets do DSKit iOS.

Os colorsets (Sources/DSKit/Resources/Colors.xcassets) são a fonte de verdade das cores
nas duas plataformas. Rode depois de mudar uma cor no DSKit iOS:

    scripts/sync-tokens.py [caminho do DSKit iOS]    # padrão: ../DSKit

Com --check, só compara e sai com erro se o arquivo gerado estiver desatualizado (CI).
"""
import json
import os
import sys

ORDER = [
    "Background", "BackgroundGrouped", "Surface", "SurfaceElevated",
    "OnSurfaceHigh", "OnSurfaceMedium", "OnSurfaceLight",
    "Border", "Hairline", "AppColorLight",
    "Accent", "AccentSecondary", "OnAccent",
    "SuccessLight", "SuccessHigh", "WarningLight", "WarningHigh",
    "InfoLight", "InfoHigh", "ErrorLight", "ErrorHigh",
    "Disabled", "OnDisabled",
]
OUT = "tokens/src/main/kotlin/io/github/romulomga/dskit/DSColorTokens.kt"


def channel(value):
    text = str(value)
    if text.startswith("0x"):
        return int(text, 16)
    number = float(text)
    return round(number * 255) if number <= 1 else int(number)


def argb(color):
    comp = color["components"]
    alpha = round(float(comp.get("alpha", "1")) * 255)
    return "0x%02X%02X%02X%02X" % (alpha, channel(comp["red"]), channel(comp["green"]), channel(comp["blue"]))


def read(catalog):
    values = {}
    for name in ORDER:
        data = json.load(open(os.path.join(catalog, name + ".colorset", "Contents.json")))
        light = dark = None
        for entry in data["colors"]:
            appearances = entry.get("appearances", [])
            if any(a.get("value") == "dark" for a in appearances):
                dark = argb(entry["color"])
            elif not appearances or any(a.get("value") == "light" for a in appearances):
                light = argb(entry["color"])
        values[name] = (light, dark or light)
    return values


def render(values):
    def prop(name):
        return name[0].lower() + name[1:]

    def block(index):
        return "\n".join(
            "        %s = Color(%s)," % (prop(name), values[name][index]) for name in ORDER
        )

    return (
        "// Gerado por scripts/sync-tokens.py a partir dos colorsets do DSKit iOS. Não edite à mão.\n"
        "package io.github.romulomga.dskit\n\n"
        "import androidx.compose.ui.graphics.Color\n\n"
        "/** Paletas clara e escura do DSKit, os mesmos valores dos colorsets do DSKit iOS. */\n"
        "public object DSColorTokens {\n"
        "    public val Light: DSColors = DSColors(\n" + block(0) + "\n    )\n\n"
        "    public val Dark: DSColors = DSColors(\n" + block(1) + "\n    )\n"
        "}\n"
    )


def main():
    args = [a for a in sys.argv[1:] if a != "--check"]
    root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    dskit = args[0] if args else os.path.join(os.path.dirname(root), "DSKit")
    catalog = os.path.join(dskit, "Sources/DSKit/Resources/Colors.xcassets")
    content = render(read(catalog))
    path = os.path.join(root, OUT)
    if "--check" in sys.argv:
        current = open(path).read() if os.path.exists(path) else ""
        if current != content:
            sys.exit("DSColorTokens.kt está desatualizado: rode scripts/sync-tokens.py")
        print("DSColorTokens.kt em dia com o DSKit iOS")
        return
    open(path, "w").write(content)
    print("gerado:", OUT)


if __name__ == "__main__":
    main()
