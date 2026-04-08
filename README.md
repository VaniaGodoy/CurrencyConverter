# Currency Converter App

Aplicativo Android desenvolvido em Kotlin para conversão de moedas em tempo real utilizando API pública.

---

## Funcionalidades

* Seleção de moeda de origem (FROM)
* Seleção de moeda de destino (TO)
* Inserção de valor para conversão
* Conversão em tempo real via API
* Exibição do resultado formatado

---

## Tecnologias utilizadas

* Kotlin
* Android Studio
* Retrofit (consumo de API)
* LiveData
* Services (Background)
* ViewBinding

---

## API utilizada

* ExchangeRate-API (gratuita)
* Endpoint: https://open.er-api.com/

---

## Arquitetura

O projeto utiliza:

* `MainActivity` → Interface do usuário
* `CurrenciesService` → Busca lista de moedas
* `ConvertService` → Realiza conversão
* `LiveData` → Atualiza a UI automaticamente

---

## Exemplo de uso

* FROM: USD
* TO: BRL
* Valor: 10

Resultado:

```
51.52
```

---

## 📸 Demonstração

[TELA PRINCIPAL]!(demo.png)

## Desenvolvido por

Vania Godoy 
Em transição de carreira para Desenvolvimento Front-End e Mobile
