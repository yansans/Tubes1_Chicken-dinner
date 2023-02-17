# Tubes1_Chicken-dinner

Implementasi bot kapal dalam permainan Galaxio dengan menggunakan strategi greedy untuk memenangkan permainan

## Table of Contents

- [Tubes1\_Chicken-dinner](#tubes1_chicken-dinner)
  - [Table of Contents](#table-of-contents)
  - [Aplikasi algoritma greedy dalam permainan Galaxio](#aplikasi-algoritma-greedy-dalam-permainan-galaxio)
  - [Installing / Getting started](#installing--getting-started)
  - [How to run?](#how-to-run)
    - [Mempersiapkan file-file yang dibutuhkan](#mempersiapkan-file-file-yang-dibutuhkan)
    - [Memasukkan bot yang telah dibuat agar bisa dijalankan](#memasukkan-bot-yang-telah-dibuat-agar-bisa-dijalankan)
    - [Menjalankan program](#menjalankan-program)
    - [Menjalankan visualiser](#menjalankan-visualiser)
  - [Made by](#made-by)

## Aplikasi algoritma greedy dalam permainan Galaxio

Bot akan mengkalkulasikan prioritas dari setiap tindakan yang dapat ia lakukan. Hasil kalkulasi tersebut akan digunakan bot untuk memilih tindakan yang paling tepat pada saat itu tanpa memikirkan konsekuensi dari tindakan tersebut kedepannya.

Bot akan mengelompokkan tindakan-tindakan yang dapat ia lakukan kedalam beberapa kategori, sehingga kalkulasi prioritas dapat dilakukan per kategori.

## Installing / Getting started

Program memerlukan:

- Compiler Java (minimal Java 11), untuk instalasinya dapat dilihat lebih lanjut pada https://www.oracle.com/java/technologies/downloads/#java8
- IntelIiJ IDEA, untuk instalasinya dapat dilihat lebih lanjut pada https://www.jetbrains.com/idea/
- .Net Core 3.1, untuk instalasinya dapat dilihat lebih lanjut pada https://docs.google.com/document/d/1Ym2KomFPLIG_KAbm3A0bnhw4_XQAsOKzpTa70IgnLNU/edit#
- maven, untuk instalasinya dapat dilihat lebih lanjut pada https://phoenixnap.com/kb/install-maven-windows

## How to run?

### Mempersiapkan file-file yang dibutuhkan

Pertama-tama clone dulu repository ini, caranya adalah dengan membuka terminal pada folder dimana kalian mau programnya diclone. Setelah terminal dibuka maka masukan command ini ke terminal:

```shell
git clone https://github.com/yansans/Tubes1_Chicken-dinner.git
```

Kemudian, download juga latest release starter pack.zip dari tautan berikut:
https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2

Kemudian extract starter pack.zipnya kedalam directory yang kalian inginkan

### Memasukkan bot yang telah dibuat agar bisa dijalankan

Untuk memasukkan bot yang telah dibuat, masuk kedalam folder repository yang telah kalian clone. Kemudian copy file bernama target dan paste kedalam directory:

```shell
<directorykalian>/starter-pack/starter-bots/JavaBot
```

Kemudian jika kalian mendapat notifikasi tentang rewrite existing files, pilih yes

### Menjalankan program

Pertama-tama kalian dapat mengatur jumlah bot yang akan kalian gunakan dengan memodifikasi:

- BotCount pada appsettings.json (pada directory /starter-pack/engine-publish/)
- BotCount pada appsettings.json (pada directory /starter-pack/runner-publish/)
Sesuaikan BotCount dengan kebutuhan kalian, dan pastikan bahwa BotCount di kedua file tersebut sama

Untuk menjalankan program, buatlah file .bat pada folder starter-pack yang berisikan:

```shell
@echo off
:: Game Runner
cd ./runner-publish/
start "" dotnet GameRunner.dll

:: Game Engine
cd ../engine-publish/
timeout /t 1
start "" dotnet Engine.dll

:: Game Logger
cd ../logger-publish/
timeout /t 1
start "" dotnet Logger.dll

:: Bots
timeout /t 3
cd ../reference-bot-publish/
timeout /t 3
start "" dotnet ReferenceBot.dll
timeout /t 3
start "" dotnet ReferenceBot.dll
timeout /t 3
start "" dotnet ReferenceBot.dll
cd ../

:: New Bots
cd ./starter-bots/JavaBot/target/
timeout /t 3
start java -jar Chicken_dinner.jar
cd../
cd../
cd../

pause
```

Jika kalian ingin menambahkan new bots, maka hapus salah satu ReferenceBot dan tambahkan bot baru kalian dalam format .jar seperti ini:

```shell
@echo off
:: Game Runner
cd ./runner-publish/
start "" dotnet GameRunner.dll

:: Game Engine
cd ../engine-publish/
timeout /t 1
start "" dotnet Engine.dll

:: Game Logger
cd ../logger-publish/
timeout /t 1
start "" dotnet Logger.dll

:: Bots
timeout /t 3
cd ../reference-bot-publish/
timeout /t 3
start "" dotnet ReferenceBot.dll
timeout /t 3
start "" dotnet ReferenceBot.dll
cd ../

:: New Bots
cd ./starter-bots/JavaBot/target/
timeout /t 3
start java -jar Chicken_dinner.jar
timeout /t 3
start java -jar JavaBot.jar
cd../
cd../
cd../

pause
```

Kemudian kalian dapat menjalankan programnya dengan menjalankan file .bat yang telah kalian buat

### Menjalankan visualiser

Masuk ke dalam folder \starter-pack\visualiser\, dan extract file .zip sesuai dengan sistem operasi anda. Kemudian masuk ke dalam folder yang telah di-extract tersebut dan jalankan aplikasi Galaxio.

Pada aplikasi Galaxio, masuk ke opsi options. Kemudian pada kolom log files location masukkan path folder logger-publish pada folder starter-pack kalian dan tekan save. Kemudian restart Galaxio kalian.

Untuk menjalankan visualisasi, masuk ke opsi load dan kemudian pilih game log yang ingin kalian visualisasikan pada kolom game log. Setelah memilih gamelog, tekan load dan visualiser akan memvisualisasikan gamelog kalian.

## Made by

Kelompok Chicken dinner:

- Athif Nirwasito         (13521053)
- Louis Caesa Kesuma      (13521069)
- Yanuar Sano Nur Rasyid  (13521110)
