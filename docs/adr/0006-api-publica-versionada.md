# 0006 — API pública versionada

**Data:** 2026-10 · **Situação:** proposta, bloqueada

## Contexto

Com `explicitApi()`, o que é público é intencional, mas uma mudança de assinatura ainda passa despercebida num PR
grande, e o app só descobre ao atualizar.

## Decisão

A validação de ABI do Kotlin (`abiValidation()`, que vem no plugin do Kotlin) grava a API pública de `tokens` e
`components` em `api/*.api`. O CI roda `checkKotlinAbi`, que falha se o código mudou a API e o arquivo não. Quem
muda a API roda `updateKotlinAbi` e o diff do arquivo vai no PR.

## Consequências

- Toda mudança de API fica visível na revisão e no histórico do git.
- Quebra de API exige uma entrada no `CHANGELOG.md` com a migração.

## Bloqueio (2026-10-11)

A `abiValidation()` do Kotlin 2.4 cria as tarefas, mas com o Kotlin embutido do AGP 9 não recebe as classes
compiladas: o dump sai vazio e o `updateKotlinAbi` falha. Alternativas: o plugin
`org.jetbrains.kotlinx.binary-compatibility-validator` ou o Metalava, ambos fora do AndroidX (ver
[0002](0002-so-androidx.md)), ou esperar o suporte no plugin do Kotlin.
