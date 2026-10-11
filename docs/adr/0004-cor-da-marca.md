# 0004 — Cor da marca pelo Material Theme Builder

**Data:** 2026-10 · **Situação:** aceita

## Contexto

O Material 3 calcula 30 papéis de cor a partir de uma cor de origem, em tons que passam no contraste. Fazer esse
cálculo dentro do app exige a biblioteca de cor do Material (não publicada como AndroidX) ou o MaterialKolor.

## Decisão

O app gera os esquemas no Material Theme Builder e os passa ao `DSBrand`: claro e escuro, e, se quiser, os de
contraste médio e alto, que o `DSTheme` escolhe pelo "Contraste de cor" do Android 14+ (sem o alto, vale o médio;
sem o médio, o padrão). `dynamicColor = true` troca a marca pelas cores do papel de parede no Android 12+. Sucesso,
aviso e informação, que o Material não tem, ficam em `DSStatusColors`, no formato das "custom colors" do builder.

## Consequências

- Mudar a cor da marca é exportar de novo do builder, não mudar uma constante.
- As cores de status não variam com o contraste; as padrão já passam de 4,5:1.
