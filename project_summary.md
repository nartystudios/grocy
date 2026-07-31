# Project Summary - Grocy Multi Android App

## 🎯 Objetivo do Projeto
Criar uma versão "multi-instância" da app Grocy Android que permita:
- Instalar side-by-side com a app original do Grocy (sem conflitos de package name)
- Gerir múltiplas instâncias/servidores Grocy na mesma app
- Fazer login em diferentes servidores sem precisar de reconfigurar

---

## 🏗️ Arquitetura Técnica

### Stack Tecnológica
- **Linguagem:** Java (com suporte a Kotlin quando necessário)
- **Arquitetura:** MVVM + Android Jetpack
  - ViewModel, LiveData, Room Database, Navigation Component
- **UI:** XML Layouts, ViewBinding, Material Design 3
- **Networking:** Volley
- **Async:** RxJava3 para operações assíncronas
- **Database:** Room (para persistência local)

### Package Name
- **Antigo:** `xyz.zedler.patrick.grocy` (conflita com app original)
- **Novo:** `xyz.zedler.patrick.grocy.multi` ✅
- **Namespace:** `xyz.zedler.patrick.grocy.multi` ✅

### MinSdk / TargetSdk
- `minSdkVersion`: 23 (Android 6.0 Marshmallow)
- `targetSdkVersion`: 37 (Android 14)
- `compileSdkVersion`: 37

---

## 📝 Alterações Implementadas

### 1. **Mudança de Package Name** ✅
- Alterado `namespace` e `applicationId` no `app/build.gradle`
- Todos os ficheiros Java movidos do pacote `xyz.zedler.patrick.grocy.*` para `xyz.zedler.patrick.grocy.multi.*`
- Todas as referências de imports atualizadas (485 ficheiros)
- Referências ao layout corrigidas: `xyz.zedler.patrick.grocy.multi.R.layout.xxx`

### 2. **Modelo Server.java** ✅
```java
@Entity(tableName = "servers")
public class Server {
    @PrimaryKey @NonNull private String id;          // UUID único
    private String displayName;                       // Nome legível
    private String grocyServerUrl;                    // URL do servidor Grocy
    private String grocyApiKey;                       // Chave API
    private String homeAssistantServerUrl;            // URL HA (opcional)
    private String homeAssistantLongLivedToken;       // Token HA (opcional)
    private int status;                               // 0=UNKNOWN, 1=CONNECTED
    private long lastUsedTimestamp;                   // Última utilização
    private boolean isDefault;                        // Servidor padrão
}
```

### 3. **Database - ServerDao** ✅
- CRUD completo para Server entities
- Método `getActiveServer()` para obter servidor ativo
- Múltiplos queries com RxJava3

### 4. **SharedPreferences Migration** 🔧 (Bug fixado)
- **Problema:** Credenciais guardadas em ficheiro privado `"credentials"`, mas verificações de navegação lia do default SharedPreferences
- **Solução:** `PrefsUtil.isServerUrlEmpty()` agora lê do ficheiro `"credentials"`

### 5. **Fragments Implementados** ✅
- **ServerSelectionFragment.java:** Lista de servidores + botão para adicionar
- **AddEditServerInstanceFragment.java:** Formulário para adicionar/editar servidor
- **Navigation Flow:** Settings > Server → ServerSelectionFragment

### 6. **ViewModel Updates** ✅
- `LoginRequestViewModel.java` atualizado para:
  - Guardar server instance na Room database
  - Definir servidor como ativo após login
  - Métodos de gestão de servidores

---

## 🗂️ Estrutura de Pastas Principais

```
app/src/main/java/xyz/zedler/patrick/grocy.multi/
├── activity/
│   ├── MainActivity.java
│   └── SplashActivity.java
├── fragment/
│   ├── ServerSelectionFragment.java          ← Novo
│   ├── AddEditServerInstanceFragment.java    ← Novo
│   ├── SettingsCatServerFragment.java        ← Atualizado com botão de gestão
│   └── ... (restante dos fragments)
├── model/
│   ├── Server.java                           ← Novo modelo
│   └── ... (outros modelos)
├── dao/
│   ├── ServerDao.java                        ← Novo DAO
│   └── ... (outros DAOs)
├── repository/
│   └── MainRepository.java                   ← Atualizado com métodos de server
├── util/
│   ├── PrefsUtil.java                        ← Atualizado para usar credenciais file
│   └── NavUtil.java                          ← Atualizado para contexto correto
└── viewmodel/
    └── LoginRequestViewModel.java            ← Atualizado com server management
```

---

## 🐛 Bugs Corrigidos

### 1. **Bug: Credenciais em SharedPreferences Errado** 🔧
- **Problema:** `LoginRequestViewModel` guardava credenciais no ficheiro `"credentials"`, mas `NavUtil.updateStartDestination()` lia do default SharedPreferences → app redirecionava sempre para login
- **Solução:** Alterado `PrefsUtil.isServerUrlEmpty(Context)` para ler do ficheiro `"credentials"`

### 2. **Bug: Navigation Incorrecta** 🔧
- **Problema:** Após selecionar servidor, a app ficava presa no menu de seleção
- **Solução:** Navegação correta para `navigation_login` após seleção de servidor

### 3. **Bug: isServerUrlEmpty(this) em Callbacks Anónimos** 🔧
- **Problema:** Dentro de `OnBackPressedCallback` e lambdas, `this` referia-se ao callback e não à Activity → compilação falhava
- **Solução:** Alterado para `MainActivity.this`

### 4. **Bug: NavHostFragment Import** 🔧
- **Problema:** Import incorreto `androidx.navigation.NavHostFragment`
- **Solução:** Alterado para `androidx.navigation.fragment.NavHostFragment`

---

## 📊 Estado Atual das Funcionalidades

| Funcionalidade | Estado | Notas |
|---|---|---|
| ✅ Mudança de Package Name | Completo | `xyz.zedler.patrick.grocy.multi` |
| ✅ Modelo Server.java | Completo | Room entity com campos necessários |
| ✅ CRUD Servers (DAO) | Completo | SQLite + RxJava3 |
| ✅ Adicionar Servidor (QR/Form) | Completo | Via AddEditServerInstanceFragment |
| ✅ Login com Credenciais | Completo | Usa ficheiro privado `"credentials"` |
| ✅ Server Selection Screen | Completo | Settings > Server → Manage Servers |
| ✅ Editar/Excluir Servidores | Completo | Via mesmo fragment de adicionar |
| 🚀 Multiple Instances Side-by-Side | Pronto | Package name diferente permite instalação conjunta |

---

## 🔧 Workflow CI/CD

### GitHub Actions
- **Workflow principal:** `.github/workflows/ci.yml`
  - Job `build-and-lint`: Compila e lint
  - Job `distribute`: Firebase App Distribution
  
### Firebase App Distribution
- **App ID:** `1:1011152716845:android:a0f7c2b78add354e4b2a4` (atualizar se package name mudar)
- **Secrets necessários:**
  - `FIREBASE_APP_ID`
  - `FIREBASE_SERVICE_ACCOUNT` (service account JSON)

### Nightly Builds
- Workflow em `.github/workflows/android-nightly.yml`
- Gera APKs de debug para testing noturno

---

## 📁 Ficheiros Críticos Para Revisão

```bash
# Build Configuration
app/build.gradle                          # namespace, applicationId, dependencies

# Database
app/src/main/java/.../model/Server.java   # Entity definition
app/src/main/java/.../dao/ServerDao.java  # DAO interface com Room
app/src/main/java/.../database/AppDatabase.java  # Database singleton

# UI Navigation
app/src/main/res/navigation/navigation_main.xml  # Nav graph completo
app/src/main/java/.../fragment/ServerSelectionFragment.java  # List servers
app/src/main/java/.../fragment/AddEditServerInstanceFragment.java  # Add/Edit server

# Utilities (Critical for Login Flow)
app/src/main/java/.../util/PrefsUtil.java    # isServerUrlEmpty() fix required
app/src/main/java/.../util/NavUtil.java      # updateStartDestination() needs Context

# ViewModels
app/src/main/java/.../viewmodel/LoginRequestViewModel.java  # Server management methods
```

---

## 🔑 Pontos Críticos a Ter em Conta

### 1. **SharedPreferences Files**
- **Default:** `PreferenceManager.getDefaultSharedPreferences()` → Para configurações gerais
- **Credentials:** `getSharedPreferences("credentials", MODE_PRIVATE)` → Guarda server_url, api_key, etc.
- **Importante:** Todas as verificações de credenciais devem usar o ficheiro `"credentials"`!

### 2. **Navigation Component**
- Usa `NavHostFragment.findNavController(this).popBackStack()` para navegação
- Direções geradas automaticamente: `XXXDirections.actionYYYtoZZZ()`
- Action popUpTo necessário em ServerSelectionFragment → navigation_login

### 3. **RxJava + Room**
- Todos os operações de banco usam RxJava3 (`Single<T>`, `Flowable<T>`)
- Observadores usam `observe(getViewLifecycleOwner(), ...)` para evitar memory leaks

### 4. **Context Issues em Callbacks**
- Dentro de `OnBackPressedCallback`, lambdas, inner classes: usar `MainActivity.this` ou `activity`
- Nunca usar `this` diretamente se estiveres dentro de um callback anónimo!

---

## 🚀 Próximos Passos Recomendados

1. **Testar fluxo completo:** Adicionar servidor → Login → Verificar que vai para OverviewStartFragment
2. **Testar múltiplas instâncias:** Adicionar segundo servidor, fazer logout, escolher outro servidor
3. **Verificar Firebase App Distribution:** Configurar `FIREBASE_APP_ID` correto
4. **Adicionar ícone de gestão no Drawer/Main menu** (se necessário)
5. **Documentação de utilizador:** Como adicionar/editar/suspender servidores

---

## 📝 Notas para Desenvolvimento Futuro

### Convenções Seguidas
- ✅ Sempre usar `@NonNull`/`@Nullable` do `androidx.annotation`
- ✅ Operações de rede/I/O em background thread (via RxJava Schedulers)
- ✅ ViewModels sobrevivem a rotações de ecrã
- ✅ Usar `viewLifecycleOwner` em Fragments
- ✅ Fechar recursos em try-with-resources

### Padrões de Código
- `viewModel.selectServerInstance(server)` após login bem-sucedido
- `NavHostFragment.findNavController(this).popBackStack()` para voltar atrás
- `activity.navUtil.navigate(XXXDirections.action...)` para navegação forward

### Dependências Importantes
```gradle
implementation(libs.room.runtime)
implementation(libs.room.rxjava3)
annotationProcessor(libs.room.compiler)
implementation(libs.rxandroid)
implementation(libs.navigation.fragment)
implementation(libs.navigation.ui)
```

---

## 📞 Contacto / Repositório

- **Repositório:** https://github.com/nartystudios/grocy
- **Branch principal:** `master`
- **Package name atual:** `xyz.zedler.patrick.grocy.multi`

---

*Última atualização: Julho 2026*  
*Versão da App: 3.9.0 (versionCode: 65)*
