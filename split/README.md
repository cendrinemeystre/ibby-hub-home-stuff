# 💸 WhatsApp Split Calculator

A small Java CLI tool to split expenses between two people based on a WhatsApp chat export (`.zip`).

---

## Features

* Reads WhatsApp chat exports (`.zip`)
* Supports 2 people
* Calculates totals and who owes whom
* Supports category shortcuts:

  ```
  1=groceries
  ```

  ```
  Alice: 10.20, 1
  ```

---

## Input

Export your WhatsApp chat **without media** and use messages like:

```
1=groceries
2=rent

Alice: 12.50, groceries
Bob: 8.00, 1
```

---

## 📊 Example Output

```
Total Alice: 120.50
Total Bob: 80.00
Bob schuldet: 20.25
------------------------------
groceries:    Alice:70.00 | Bob:30.00
```

---

## ⚠️ Notes

* Only supports 2 people
* Input format must match:

  ```
  Name: amount, category
  ```
* Works with default WhatsApp export format

---

## 🛠️ Requirements

* Java 11+
* WhatsApp chat export (`.zip`)

---

## 🚀 Get Started

👉 **[⬇️ Download split.jar](https://github.com/cendrinemeystre/ibby-hub-home-stuff/-/releases/split.jar)**

Then run:

```bash
java -jar split.jar chat.zip Alice Bob

## 📄 License

MIT
