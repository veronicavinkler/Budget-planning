# Budget Planning (Eelarve planeerimise rakendus)

## 1. Projekti üldinfo
* **Projekti nimi:** Budget Planning
* **Lühikirjeldus:** Mobiilirakendus, mis aitab kasutajatel jälgida oma igapäevaseid tulusid ja kulusid, määrata eelarveid ning saada selge visuaalne ülevaade oma finantsolukorrast.
* **Meeskonna liikmed:** Veronica Vinkler

---

## 2. Projekti eesmärk
* **Mis probleemi rakendus lahendab?** Paljudel inimestel on raskusi oma igakuiste kulutuste jälgimisega, mis viib tihti ülekulutamiseni. Rakendus lahendab selle probleemi, pakkudes lihtsat ja kiiret viisi finantside protokollimiseks ning analüüsimiseks, hoides ära "kuhu mu raha kadus?" sündroomi.
* **Kellele rakendus on mõeldud?** Rakendus on mõeldud kõigile, kes soovivad saavutada paremat kontrolli oma isikliku või leibkonna eelarve üle — alates tudengitest kuni peredeni.

---

## 3. Funktsionaalsus
### Peamised võimalused:
* **Tehingute haldamine:** Tulude ja kulude kiire lisamine, muutmine ja kustutamine.
* **Kategooriad:** Kulutuste rühmitamine (nt toit, transport, meelelahutus, elamiskulud).
* **Eelarve seadmine:** Igakuiste või nädalaste limiitide seadmine konkreetsetele kategooriatele.
* **Visuaalsed graafikud:** Dünaamilised diagrammid, mis näitavad kulude jagunemist protsentuaalselt.

### Olulisemad kasutusjuhtumid (Use Cases):
1. **Kulu kiire registreerimine:** Kasutaja sooritab poest ostu, avab rakenduse, sisestab summa, valib kategooriaks "Toit" ja salvestab tehingu vähem kui 10 sekundiga.
2. **Limiidi ületamise hoiatus:** Kasutaja seab meelelahutuse eelarveks 50€. Kui ta üritab lisada uut kulu, mis ületab selle limiidi, teavitab rakendus teda eelarve täitumisest.
3. **Kuine ülevaade:** Kuu lõpus vaatab kasutaja statistika vaadet, et analüüsida, millises kategoorias tehti kõige suuremaid kulutusi.

---

## 4. Kasutatud tehnoloogiad
* **Programmeerimiskeel:** Kotlin
* **Kasutajaliides (UI):** Jetpack Compose
* **Arhitektuuri komponendid:** ViewModel, LiveData / Flow, Navigation component

---

## 5. Andmete haldamine
* **Kust andmed pärinevad?** Kõik andmed pärinevad otse kasutajalt (sisestatud tehingud, loodud kategooriad ja määratud limiidid).
* **Kuidas andmeid salvestatakse?** [Andmed salvestatakse lokaalselt seadmesse, kasutades Room andmebaasi, tagades privaatsuse ja võrguühenduseta töö / Andmed sünkroniseeritakse pilves asuvasse Firebase andmebaasi - asenda õigega].
* **Kuidas toimub andmete laadimine ja uuendamine?** Andmete laadimine toimub asünkroonselt, kasutades Kotlin Coroutines tehnoloogiat. UI jälgib andmebaasi muudatusi reaalajas [Flow / LiveData] abil — kui andmebaasis midagi muutub, uueneb vaade automaatselt ilma rakendust taaskäivitamata.

---

## 6. Rakenduse arhitektuur
Projekt järgib **MVVM (Model-View-ViewModel)** ja Clean Architecture põhimõtteid, mis jaguneb järgmisteks kihtideks:

* **UI kiht (View):** Koosneb [Compose vaadetest / Activitydest ja Fragmentidest - asenda õigega], mis tegelevad ainult andmete kuvamise ja kasutaja sisendi vastuvõtmisega.
* **Äriloogika kiht (ViewModel):** Vahendab andmeid andmekihi ja UI vahel, säilitab ekraani olekut (UI State) ning tegeleb sisestatud andmete valideerimisega.
* **Andmekiht (Data/Repository):** Koondab endas andmeallikad. Repository muster otsustab, kas andmed võetakse lokaalsest andmebaasist või välisest API-st.
* **Andmebaasi ühendused:** [Room DAO (Data Access Object) - asenda õigega] liidesed, mis käivitavad SQL päringuid andmete kirjutamiseks ja lugemiseks.

---

## 7. Rakenduse käivitamine

### Eeltingimused
* Arvutisse on paigaldatud **Android Studio** (soovitavalt uusim versioon).
* Paigaldatud on **JDK 17** (või uuem).
* Android-seade või emulaator (API tase [näiteks 26 - asenda õigega] või kõrgem).

### Paigaldus- ja käivitusjuhend

1. **Laadi projekt alla (Klooni repositoorium):**
```bash
   git clone [https://github.com/veronicavinkler/Budget-planning.git](https://github.com/veronicavinkler/Budget-planning.git)
```

### 8. Rakendus
<img width="309" height="629" alt="image" src="https://github.com/user-attachments/assets/75db504b-0b95-489f-8946-0236be2ceb33" />
<img width="309" height="629" alt="image" src="https://github.com/user-attachments/assets/5982c90e-3cf8-46bf-a496-2a177f00feb1" />

<img width="309" height="629" alt="image" src="https://github.com/user-attachments/assets/10b46226-4d02-4442-82b5-635f0afe36c6" />
<img width="309" height="629" alt="image" src="https://github.com/user-attachments/assets/b5f89c75-4f6b-4656-b74c-c3399511bd5b" />


https://github.com/user-attachments/assets/2b027718-7b3e-4377-92ab-f46d558a676e







