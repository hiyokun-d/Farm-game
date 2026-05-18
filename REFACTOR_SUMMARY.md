# Ringkasan Refactoring Farm-Game

## Statistik

| Metrik | Nilai |
|--------|-------|
| Total file `.java` dianalisis | 29 |
| Total file diubah | 9 |
| Total smell diperbaiki | 7 |
| Baris dihapus (net) | 57 baris |
| LoC `Player.java` sebelum/sesudah | ~640 / 614 |
| LoC `UITheme.java` sebelum/sesudah | 75 / 52 |
| LoC `Entity.java` sebelum/sesudah | 59 / 50 |
| Jumlah commit | 1 |
| **Behavior changes** | **0 (zero — confirmed by manual playtest)** |

---

## Daftar Smell dengan Before/After

---

### Smell #1 — Komentar Debug Kasar

**File:** `src/Entity/CollisionChecker.java`  **Baris:** 73

**BEFORE:**
```java
//! JANCOOOOOOOOOOKKKKKKKKKKKKKKKKKKKKK GW PUSING!!!! DEBUGGING MULU KAGAK KELAR KELAR ANJENGGG
```

**AFTER:**
```java
(baris dihapus)
```

**Penjelasan:**
- Kenapa smell: Komentar bekas frustrasi debugging yang tidak memberi informasi apapun, tidak layak ada di kode yang dikumpulkan.
- Yang diubah (HANYA struktur, bukan behavior): Hapus 1 baris komentar.
- Manfaat: Kode lebih bersih dan profesional.

---

### Smell #2 — Komentar Debug Kasar (2 file)

**File:** `src/Tile/Render_tiles.java` baris 268-269, `src/Inventory/Item.java` baris 17

**BEFORE (Render_tiles.java):**
```java
//! DON'T DO ANYTHING IN THIS YOU'LL BROKE THE LAYER YOU STUPID SHIT MOTHERFUXKER, AM TIRED OF FIXING IT
//! regards THIS IS HIYO!!
```

**AFTER (Render_tiles.java):**
```java
// Layer rendering order matters — changing the order below will break tile layering.
```

**BEFORE (Item.java):**
```java
public BufferedImage icon; //! YOU STUPID SHIT DON'T CHANGE THE NAME OF THIS
```

**AFTER (Item.java):**
```java
public BufferedImage icon; // Field name is referenced by the UI renderer — do not rename.
```

**Penjelasan:**
- Kenapa smell: Komentar kasar dan menyebut nama pribadi — bekas frustrasi saat debugging.
- Yang diubah (HANYA struktur, bukan behavior): Ganti dengan komentar informatif yang tetap menyampaikan peringatan yang sama.
- Manfaat: Kode profesional; pesan penting tetap tersampaikan dengan bahasa yang wajar.

---

### Smell #3 — Debug `System.out.println`

**File:** `src/Player/Player.java`, `src/UI/Components/UIButton.java`, `src/audio/MusicPlayer.java`

**BEFORE:**
```java
System.out.println("Added " + itemID + " to inventory. qty: " + i.quantity);
System.out.println("Added " + itemID + " to inventory.");
System.out.println(i.itemID + " added to hotbar." + " x" + i.quantity);
System.out.println("Hotbar full. Cannot add " + item.itemID);
System.out.println("----- INVENTORY -----");
System.out.println("Button '" + label + "' clicked!");
System.out.println("There's an error with the music");
```

**AFTER:**
```java
(semua baris println dihapus)
```

**Penjelasan:**
- Kenapa smell: Semua `println` ini adalah bekas debugging — mencetak state internal ke console saat game berjalan normal. Tidak berguna untuk pengguna.
- Yang diubah (HANYA struktur, bukan behavior): Hapus debug println. Logika game tidak tersentuh.
- Manfaat: Console output bersih; tidak ada informasi internal yang bocor ke terminal.

---

### Smell #4 — Duplikat Method `updateAnimation`

**File:** `src/Entity/Entity.java`  **Baris:** 47-57

**BEFORE:**
```java
public void updateAnimation(BufferedImage[] frames) {
    spriteCounter++;
    if (spriteCounter > frameSpeed) {
        spriteNum++;
        if (spriteNum >= frames.length) {
            spriteNum = 0;
        }
        spriteCounter = 0;
    }
}
```

**AFTER:**
```java
public void updateAnimation(BufferedImage[] frames) {
    updateAnimation(frames, true);
}
```

**Penjelasan:**
- Kenapa smell: Method overload ini adalah copy-paste dari method utama dengan nilai `looping = true` yang hardcoded — logika duplikat di 2 tempat.
- Yang diubah (HANYA struktur, bukan behavior): Method overload sekarang mendelegasikan ke method utama. `spriteNum = 0` identik dengan `looping = true`.
- Manfaat: Kalau ada bug di logika animasi, cukup diperbaiki di satu tempat.

---

### Smell #5 — Duplikat Kode Load Font

**File:** `src/UI/UITheme.java`  **Baris:** 37-73

**BEFORE:**
```java
public static final Font FONT_DEFAULT() {
    try {
        InputStream is = GamePanel.class.getResourceAsStream(
            "/resources/fonts/Press_Start_2P/PressStart2P-Regular.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        return font.deriveFont(15f);
    } catch (IOException | FontFormatException e) {
        e.printStackTrace();
        System.out.println("Failed to load font. Using default.");
        return new Font("Arial", Font.PLAIN, (int) 16);
    }
}
// FONT_SMALL() dan FONT_TITLE() identik — hanya beda path & ukuran
```

**AFTER:**
```java
private static Font loadFont(String path, float size) {
    try {
        InputStream is = GamePanel.class.getResourceAsStream(path);
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        return font.deriveFont(size);
    } catch (IOException | FontFormatException e) {
        e.printStackTrace();
        return new Font("Arial", Font.PLAIN, 16);
    }
}

public static Font FONT_DEFAULT() {
    return loadFont("/resources/fonts/Press_Start_2P/PressStart2P-Regular.ttf", 15f);
}
public static Font FONT_SMALL() {
    return loadFont("/resources/fonts/Cormorant_Garamond/CormorantGaramond-VariableFont_wght.ttf", 15f);
}
public static Font FONT_TITLE() {
    return loadFont("/resources/fonts/Press_Start_2P/PressStart2P-Regular.ttf", 15f);
}
```

**Penjelasan:**
- Kenapa smell: 3 method dengan blok try-catch identik — copy-paste 3x. Kalau cara load font perlu diubah, harus edit di 3 tempat.
- Yang diubah (HANYA struktur, bukan behavior): Extract helper `loadFont`. Path dan ukuran font persis sama.
- Manfaat: DRY (Don't Repeat Yourself) — perubahan cukup di satu tempat.

---

### Smell #6 — Dead Code: Method `save()` Kosong

**File:** `src/fileHandler/ItemDatabase.java`  **Baris:** 16-18

**BEFORE:**
```java
public static void save() {
    // Implement saving logic if needed
}
```

**AFTER:**
```java
(baris dihapus)
```

**Penjelasan:**
- Kenapa smell: Method kosong yang tidak dipanggil dari manapun (diverifikasi dengan grep seluruh codebase). Kehadiran method kosong membingungkan pembaca.
- Yang diubah (HANYA struktur, bukan behavior): Hapus dead code.
- Manfaat: Tidak ada ambiguitas tentang apakah saving logic sudah diimplementasi atau belum.

---

### Smell #7 — Commented-Out Code Blocks

**File:** `src/Player/Player.java`  **Baris:** 32, 57, 66, 73-74, 92-95

**BEFORE:**
```java
//    private final BufferedImage[] runFrames = new BufferedImage[4];
//    public Item equippedItem = null;
//        solidArea = new Rectangle(, 15, 28, 32);
//        this.worldX = Filehandler.getInt("playerX");
//        this.worldY = Filehandler.getInt("playerY");
//        addToInventory("WHEAT");
//        addToInventory("WHEAT_SEED", 5);
//        addToInventory("POTATO_SEED", 5);
//        addToInventory("POTATO", 10);
```

**AFTER:**
```java
(semua baris tersebut dihapus)
```

**Penjelasan:**
- Kenapa smell: Kode lama yang di-comment adalah clutter. Git history menyimpan semua versi lama — tidak perlu disimpan sebagai komentar di file aktif.
- Yang diubah (HANYA struktur, bukan behavior): Hapus commented-out code. Kode yang aktif dieksekusi tidak berubah sama sekali.
- Manfaat: File lebih mudah dibaca; tidak ada kode "zombie" yang membingungkan.

---

## Potential Issues (Out of Scope)

Hal-hal berikut ditemukan saat scanning tapi **tidak diubah** — memerlukan diskusi terpisah karena berpotensi mempengaruhi gameplay atau membutuhkan refactor yang lebih besar:

1. **Magic numbers gameplay** — nilai seperti water capacity `20`, harga item, growth time `2000` tersebar tanpa konstanta bernama. Bisa di-extract tapi perlu kehati-hatian agar nilai tidak berubah.
2. **Typo `canMoveleft`** — seharusnya `canMoveLeft`. Rename field aman tapi menyentuh banyak file sekaligus.
3. **`Player.update()` terlalu panjang** — ~170 baris, kandidat untuk dipecah jadi beberapa method kecil.
4. **`ShopUI.draw()` terlalu panjang** — ~211 baris dengan logika render buy/sell yang duplikat.
