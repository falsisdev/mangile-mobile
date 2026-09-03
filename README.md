<div align="center">

# 📱 Mangile Mobile

**Kotlin Multiplatform (KMP)** ve **Compose Multiplatform** ile geliştirilmiş resmi Mangile Android ve iOS mobil uygulaması.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7.3-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

</div>

---

## 📖 Hakkında

Mangile Mobile, [Mangile](https://github.com/falsisdev/mangile) web ekosisteminin mobil deneyimidir. Manga, manhwa, manhua ve hafif romanları (light novel) telefon ve tabletlerde en yüksek performans, akıcı animasyonlar ve modern bir arayüzle okumanızı sağlar.

Kod tabanının **%95'inden fazlası** Android ve iOS arasında ortak (shared) Kotlin ve Jetpack Compose kodu olarak çalışır.

---

## ✨ Özellikler

- 📚 **Manga, Manhwa & Light Novel Desteği**: Mangile veritabanındaki tüm popüler ve yeni içerikler.
- 🖼️ **Gelişmiş Manga & Webtoon Okuyucu**:
  - **Webtoon Modu**: Dikey, kesintisiz sonsuz kaydırma.
  - **Sayfalı Mod**: Klasik yatay sayfa çevirme.
  - **Pinch-to-Zoom**: Çift parmak veya çift dokunuşla akıcı yakınlaştırma/kaydırma.
- 📖 **Novel Okuyucu (Text Reader)**:
  - Özelleştirilebilir font boyutu ve satır aralığı.
  - Font ailesi tercihi (Düz, Serif, Monospace).
  - Gece ve gündüz okuma konforu.
- 🔍 **Keşfet & Arama**: Başlığa, türe ve etiketlere ("Ödüllü", "Macera", "Dram" vb.) göre anlık arama ve filtreleme.
- 🎨 **Mangile Tasarım Sistemi**: Web platformu ile birebir uyumlu Mist/Emerald teması.

---

## 🛠️ Mimari ve Teknolojiler

- **UI**: Compose Multiplatform (Material 3)
- **Ağ Katmanı**: [Ktor Client](https://ktor.io/) + Darwin (iOS) & OkHttp (Android) motorları
- **Görsel Yükleme & Önbellek**: [Coil 3](https://coil-kt.github.io/coil/) (Compose Multiplatform)
- **JSON Serileştirme**: `kotlinx.serialization`
- **Asenkron Programlama**: `kotlinx.coroutines`

---

## 🚀 Başlarken

### Gereksinimler
- macOS (iOS derlemesi için)
- JDK 17 veya JDK 21
- Android Studio Ladybug / Koala veya daha yenisi
- Xcode 15+ (iOS simülatörü için)

### Projeyi Klonlayın
```bash
git clone https://github.com/falsisdev/mangile-mobile.git
cd mangile-mobile
```

### Android Uygulamasını Başlatma
Android Studio üzerinden projeyi açabilir veya terminalden derleyebilirsiniz:
```bash
./gradlew installDebug
```

### iOS Uygulamasını Başlatma
Xcode üzerinden `iosApp/iosApp.xcodeproj` dosyasını açıp simülatörde çalıştırabilirsiniz:
```bash
open iosApp/iosApp.xcodeproj
```

---

## 📄 Lisans

Bu proje **Apache 2.0 Lisansı** ile lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakabilirsiniz.
