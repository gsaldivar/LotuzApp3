# LotuzApp3

![Android API](https://img.shields.io/badge/Android%20API-34-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7f52ff)
![License](https://img.shields.io/badge/License-MIT-blue)

## Descripción
Aplicación móvil de e‑commerce en Android (Kotlin + XML) con dos roles: Cliente y Administrador. Incluye autenticación con token, catálogo, carrito, carga de productos con imagen, y gestión de órdenes pendientes. El backend consumido es Xano (REST), configurable mediante `BuildConfig`.

## Requisitos
- Android Studio (Koala o superior)
- JDK 17
- Kotlin 2.0.21 / Gradle 8.13
- `compileSdk=34`, `minSdk=24`
- Conexión a Internet para consumir el backend de Xano

## Configuración y ejecución
1. Clonar el repositorio
   ```bash
   git clone https://github.com/gsaldivar/LotuzApp3.git
   cd LotuzApp3
   ```
2. Abrir en Android Studio y sincronizar Gradle
3. Base URL del backend
   - La base URL se define en `app/build.gradle.kts:19` en `BuildConfig.XANO_BASE_URL`.
   - Actualmente apunta al workspace Xano `https://x8ki-letl-twmt.n7.xano.io/api:XPPncXe7/`.
4. Ejecutar en emulador o dispositivo
   - Emulador: si usas backend local, configura `http://10.0.2.2:8080/api/`.
   - Dispositivo físico: usa la IP de tu PC (`http://<tu_ip>:8080/api/`) y abre el puerto 8080.

## Endpoints consumidos (Xano)
- Autenticación
  - `POST /auth/login` → `AuthResponse` con `authToken` (`app/src/main/java/com/miapp/lotuz_2/network/XanoApi.kt:13-15`).
  - `GET /auth/me` → `User` con `role` (`app/src/main/java/com/miapp/lotuz_2/network/XanoApi.kt:23-24`).
- Productos
  - `GET /product` → lista de `Product` (`app/src/main/java/com/miapp/lotuz_2/network/XanoApi.kt:32-34`).
  - `POST /product` (multipart) → crea producto con imagen (`app/src/main/java/com/miapp/lotuz_2/network/XanoApi.kt:36-44`).
- Órdenes
  - `GET /order` → lista de `Order` (`app/src/main/java/com/miapp/lotuz_2/network/XanoApi.kt:65-67`).
  - `POST /order` → crea orden (`app/src/main/java/com/miapp/lotuz_2/network/XanoApi.kt:62-64`).

## Uso básico
### Login y ruteo por rol
```kotlin
// LoginActivity.kt:69-99
val api = com.miapp.lotuz_2.network.RetrofitClient.getInstance(this)
val res = api.login(mapOf("email" to email, "username" to email, "password" to pass))
if (res.isSuccessful) {
    val token = res.body()!!.authToken
    com.miapp.lotuz_2.utils.SessionManager(this).saveAuthToken(token)
    val me = api.getMe()
    val role = me.body()!!.role
    com.miapp.lotuz_2.ui.LoginActivity().navigateBasedOnRole(role)
}
```

### Listado de productos en Cliente
```kotlin
// ClientHomeActivity.kt:35-71
val response = com.miapp.lotuz_2.network.RetrofitClient.getInstance(this).getProducts()
if (response.isSuccessful) {
    val productList = response.body()!!
    binding.rvProducts.adapter = com.miapp.lotuz_2.ui.ProductAdapter(productList) { product ->
        com.miapp.lotuz_2.utils.CartManager(this).add(product.id)
    }
}
```

### Subida de producto con imagen (Admin)
```kotlin
// AdminAddProductActivity.kt:94-121
val namePart = name.toRequestBody("text/plain".toMediaType())
val descPart = desc.toRequestBody("text/plain".toMediaType())
val pricePart = price.toRequestBody("text/plain".toMediaType())
val stockPart = stock.toRequestBody("text/plain".toMediaType())
val file = uriToFile(selectedImageUris.first())
val requestFile = file.asRequestBody("image/jpeg".toMediaType())
val bodyImage = MultipartBody.Part.createFormData("image", file.name, requestFile)
val res = com.miapp.lotuz_2.network.RetrofitClient.getInstance(this)
    .createProduct(namePart, descPart, pricePart, stockPart, bodyImage)
```

## Pruebas
- Unitarias (JUnit):
  ```bash
  ./gradlew :app:testDebugUnitTest
  ```
- Build del APK (debug):
  ```bash
  ./gradlew :app:assembleDebug
  ```

## Contribución
1. Crea un issue describiendo la propuesta o problema.
2. Haz fork y crea una rama: `feature/<breve-descripcion>` o `fix/<breve-descripcion>`.
3. Sigue el estilo del proyecto y evita introducir secretos.
4. Abre un Pull Request contra `main` con descripción y capturas si aplica.

## Licencia
Este proyecto está licenciado bajo la licencia MIT. Consulta el archivo de licencia si se agrega en el futuro o usa este encabezado en tus contribuciones.

## Badges de estado
- Build y cobertura por Actions: no configurado por defecto. Se pueden añadir workflows de CI para generar badges de estado.
