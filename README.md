# File Download Manager

Bu proje, Java Swing ve çoklu iş parçacığı (thread) kullanarak bir dosya indirme yöneticisi uygulamasıdır. Uygulama, kullanıcıya bir veya birden fazla dosyayı eşzamanlı olarak indirme imkanı sunar. Her bir indirme işlemi ayrı bir iş parçacığında gerçekleşir ve kullanıcı her dosya için durdurma, devam ettirme ve iptal etme işlemlerini gerçekleştirebilir.

## Özellikler

- **Dinamik Link Ekleme**: Kullanıcılar istedikleri kadar indirme bağlantısı ekleyebilir.
- **Eşzamanlı İndirme**: Her dosya indirimi ayrı bir iş parçacığında (thread) yapılır.
- **İlerleme Çubuğu (Progress Bar)**: Her indirme için indirme ilerlemesini gösteren bir ilerleme çubuğu.
- **Durdur, Devam Ettir, İptal Et**: Her dosya indirimi için durdurma, devam ettirme ve iptal etme işlevleri mevcuttur.

## Proje Yapısı

### Sınıflar

- **DownloadTask.java**: Her bir dosya indirimi için bir iş parçacığı olarak çalışan sınıftır. Bu sınıf dosya indirimi yapar ve durdurma, devam ettirme, iptal etme işlevlerini içerir.
- **Main.java**: Kullanıcı arayüzünü sağlayan ana sınıftır. Kullanıcıların indirme bağlantılarını ekleyebileceği, indirme işlemini başlatabileceği ve her dosya için durdur, devam ettir ve iptal et butonlarını kullanabileceği bir arayüz içerir.

### İşlevler

- **pauseDownload()**: İndirme işlemini geçici olarak duraklatır.
- **resumeDownload()**: Durdurulmuş indirme işlemini devam ettirir.
- **cancelDownload()**: İndirme işlemini iptal eder ve ilerleme çubuğunu sıfırlar.

## Kurulum

Bu projeyi çalıştırmak için bilgisayarınızda **Java JDK** ve **Java IDE** yüklü olmalıdır.

1. **Projeyi Kopyalayın**: Bu projeyi GitHub'dan veya manuel olarak bilgisayarınıza indirin.
2. **Java IDE (Örneğin IntelliJ veya Eclipse) ile Açın**.
3. **Derleyin ve Çalıştırın**: `Main.java` dosyasını ana sınıf olarak ayarlayıp çalıştırın.

## Kullanım

1. **Yeni Link Ekle** butonuna basarak bir indirme bağlantısı ekleyin.
2. **İndirmeyi Başlat** butonuna basarak eklediğiniz tüm bağlantıları indirmeye başlayın.
3. Her dosya için:
   - **Durdur** butonuna basarak indirmeyi geçici olarak durdurun.
   - **Devam** butonuna basarak durdurulmuş indirmeyi devam ettirin.
   - **İptal** butonuna basarak indirme işlemini iptal edin.
