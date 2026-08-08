# Lab: Custom ViewResolver ใน Spring Boot + Thymeleaf

**วิชา:** CP353002 Principles of Software Design
**หัวข้อ:** การกำหนด (configure) `ViewResolver` ของ Spring Boot ที่ใช้ Thymeleaf เป็น view engine

> แล็ปนี้ให้นักศึกษา **ลงมือสร้างโปรเจกต์เองทีละไฟล์** ตามขั้นตอนด้านล่าง แล้วอัปโหลดขึ้น GitHub ของตัวเอง
> ไม่มีการต่อฐานข้อมูลใดๆ ทั้งหมดรันได้ด้วย static HTML + Thymeleaf ล้วนๆ

---

## 0. ขอบเขตงานที่ต้องส่ง (อ่านก่อนเริ่ม)

**เอกสารนี้เป็นเพียงคู่มือ/ใบงาน ไม่ใช่ repository ที่ต้องกด Fork** ไม่มี repo ต้นทางให้ fork หรือ clone มาต่อยอด — นักศึกษาต้อง**สร้าง GitHub repository ใหม่ที่ว่างเปล่าด้วยตัวเอง** แล้วสร้างไฟล์ทีละไฟล์ตามขั้นตอนในเอกสารนี้เอง (ดูหัวข้อ 4 เป็นต้นไป)

สิ่งที่ต้องส่งมีทั้งหมด 2 อย่าง:

1. **ลิงก์ GitHub repository** ของนักศึกษาเอง ที่มีอยู่ข้างในครบ:
   - ไฟล์โค้ดทั้งหมด (`pom.xml`, `.java` 3 ไฟล์, `application.properties`, `custom-templates/home.html`)
   - ไฟล์ `README.md` (จะใช้ฉบับนี้เก็บไว้ในโปรเจกต์เป็นเอกสารประกอบ หรือเขียนสรุปของตัวเองเพิ่มก็ได้ ไม่บังคับต้องเขียนใหม่)
2. **ไฟล์ `ViewResolver_Quiz.docx`** ที่กรอกคำตอบและแนบภาพหน้าจอ (screenshot) ภาคปฏิบัติครบทุกข้อแล้ว — จะใส่ไว้ใน repo เดียวกัน หรือส่งแยกตามที่อาจารย์/ผู้สอนกำหนดก็ได้

*** สำหรับไฟล์Docx อยากให้แนบมาในclassroom ด้วยเป็น link google drive เพื่อง่ายต่อTA ในการตรวจ ***

## 1. โจทย์ของแล็ป

ให้นักศึกษาศึกษาวิธีกำหนด `ViewResolver` ของ Spring Boot ที่ใช้ Thymeleaf โดยแทนที่จะใช้โฟลเดอร์ template
เริ่มต้น (`/src/main/resources/templates/`) ให้สร้าง **ViewResolver แบบกำหนดเอง (custom)** ที่ชี้ไปยังโฟลเดอร์อื่น
คือ `/src/main/resources/custom-templates/` แทน แล้วตอบคำถามท้ายแล็ปว่าใครเป็นผู้เรียกใช้ ViewResolver นี้ และเรียกเมื่อไร
(คำถามอยู่ในไฟล์ข้อสอบแยกต่างหาก ดูหัวข้อ "แบบทดสอบ" ด้านล่าง)

## 2. ทำไมเรื่องนี้ถึงสำคัญ (Principles of Software Design)

`ViewResolver` เป็นตัวอย่างที่ดีของหลักการ **separation of concerns** และ **dependency inversion** ใน MVC:

- Controller ไม่รู้จัก และไม่ควรรู้จัก path จริงของไฟล์ HTML บน disk — มันรู้แค่ "ชื่อ view" เชิงตรรกะ (logical view name) เช่น `"home"`
- หน้าที่การแปลง "ชื่อ view" ไปเป็น "ไฟล์จริง" ถูกแยกออกมาเป็นอีก component หนึ่งคือ `ViewResolver`
- เพราะแยกกันแบบนี้ เราจึงเปลี่ยน location ของ templates, เปลี่ยน naming convention, หรือสลับ template engine (Thymeleaf → FreeMarker → JSP) ได้ โดย **ไม่ต้องแก้โค้ด Controller เลยแม้แต่บรรทัดเดียว**

## 3. สิ่งที่ต้องมีก่อนเริ่ม

- JDK 17 ขึ้นไป
- Maven
- Git (และบัญชี GitHub)
- ไม่ต้องติดตั้งหรือเชื่อมต่อฐานข้อมูลใดๆ

## 4. โครงสร้างโปรเจกต์ที่ต้องสร้าง

ให้สร้างโฟลเดอร์และไฟล์ตามผังนี้ (จะสร้างด้วยวิธีไหนก็ได้ — พิมพ์คำสั่งใน terminal, คลิกขวา New Folder ใน IDE เช่น IntelliJ/VS Code, หรือสร้างผ่าน File Explorer/Finder — ผลลัพธ์ต้องได้โครงสร้างเดียวกันนี้ เดี๋ยวจะไล่อธิบายทีละไฟล์ในหัวข้อถัดไป):

```
spring-thymeleaf-demo/
 ├── pom.xml                                   <- ไฟล์ตั้งค่า Maven บอกว่าโปรเจกต์ต้องใช้ library อะไรบ้าง
 ├── .gitignore
 └── src/main/                                 <- โครงสร้างมาตรฐานของ Maven/Spring Boot ซอร์สโค้ดหลักอยู่ใต้นี้เสมอ
      ├── java/com/example/demo/                <- โฟลเดอร์ตาม package ของโค้ด Java (จุด "." ในชื่อ package แปลงเป็น "/")
      │    ├── DemoApplication.java             <- คลาสหลัก จุดเริ่มรันโปรแกรม
      │    ├── config/ThymeleafConfig.java       <- แยกโฟลเดอร์ config/ ไว้เก็บไฟล์ตั้งค่า (custom ViewResolver ประกาศที่นี่)
      │    └── controller/HomeController.java   <- แยกโฟลเดอร์ controller/ ไว้เก็บ Controller ทั้งหมด
      └── resources/                            <- ไฟล์ที่ไม่ใช่โค้ด Java เช่น ค่าคอนฟิก, template, รูปภาพ
           ├── application.properties           <- ค่าคอนฟิกของแอป เช่น port ที่รัน
           └── custom-templates/                <- โฟลเดอร์ที่กำหนดเอง ใช้แทน templates/ ซึ่งเป็นค่า default
                └── home.html                    <- ไม่ได้อยู่ใน /templates/ ปกติ (นี่คือจุดสำคัญของแล็ป)
```

**ทำไมต้องแยก `config/` กับ `controller/`:** เพื่อให้อ่านง่ายและสอดคล้องกับหลัก separation of concerns แบบเดียวกับที่ ViewResolver แยกหน้าที่ออกจาก Controller — โฟลเดอร์ `config/` เก็บเฉพาะ "การตั้งค่า" (`@Configuration` classes) ส่วน `controller/` เก็บเฉพาะ "ตัวรับ request" (`@Controller` classes) ไม่ปนกัน

ตัวอย่างการสร้างโฟลเดอร์เปล่าด้วยคำสั่ง (ทางเลือกหนึ่ง ไม่บังคับต้องใช้วิธีนี้):

```bash
mkdir -p spring-thymeleaf-demo/src/main/java/com/example/demo/config
mkdir -p spring-thymeleaf-demo/src/main/java/com/example/demo/controller
mkdir -p spring-thymeleaf-demo/src/main/resources/custom-templates
cd spring-thymeleaf-demo
```

ถ้าใช้ IDE ก็สร้างโปรเจกต์ Maven เปล่าขึ้นมาก่อน แล้วคลิกขวาสร้างโฟลเดอร์ย่อยตามผังด้านบนได้เลย ขอแค่ path/ชื่อโฟลเดอร์ตรงกัน โปรเจกต์ก็จะรันได้เหมือนกัน

## 5. ขั้นตอนที่ 1 — `pom.xml`

สร้างไฟล์ `pom.xml` ที่ root ของโปรเจกต์ ใส่ dependency แค่สองตัว: `spring-boot-starter-web` (สำหรับ DispatcherServlet/Spring MVC) และ `spring-boot-starter-thymeleaf` (template engine):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>spring-thymeleaf-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <properties>
        <java.version>17</java.version>
    </properties>
</project>
```

## 6. ขั้นตอนที่ 2 — `DemoApplication.java`

`src/main/java/com/example/demo/DemoApplication.java` — จุดเริ่มต้นของแอป จะเป็นตัว start embedded Tomcat และลงทะเบียน DispatcherServlet ให้อัตโนมัติ:

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

## 7. ขั้นตอนที่ 3 — `config/ThymeleafConfig.java` (จุดสำคัญของแล็ป)

นี่คือไฟล์ที่ **กำหนด custom ViewResolver** ให้ชี้ไปที่โฟลเดอร์ `custom-templates/` แทนค่า default `templates/`:

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/custom-templates/"); // โฟลเดอร์ที่กำหนดเอง
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); // ปิด cache ระหว่าง dev
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setOrder(1); // ลำดับความสำคัญ ถ้ามีหลาย ViewResolver ใน context เดียวกัน
        return viewResolver;
    }
}
```

`setPrefix()` และ `setSuffix()` คือจุดที่กำหนดว่า logical view name จะถูกแปลงเป็น path ไฟล์จริงอย่างไร:
ชื่อ view `"home"` + prefix `classpath:/custom-templates/` + suffix `.html`
→ ไฟล์จริงคือ `classpath:/custom-templates/home.html`

## 8. ขั้นตอนที่ 4 — `controller/HomeController.java`

```java
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello from Thymeleaf with a custom ViewResolver!");
        return "home"; // ไม่ใช่ path ไฟล์ แค่ "ชื่อ view" เชิงตรรกะเท่านั้น
    }
}
```

สังเกตว่า Controller ใช้ `@Controller` (ไม่ใช่ `@RestController`) เพื่อให้ค่าที่ return ถูกตีความเป็น
**ชื่อ view**, และ `ViewResolver` ที่เราลงทะเบียนไว้จะเป็นตัวจัดการแปลค่านี้ให้เอง

## 9. ขั้นตอนที่ 5 — `application.properties`

`src/main/resources/application.properties`:

```properties
server.port=8080
spring.thymeleaf.cache=false
```

## 10. ขั้นตอนที่ 6 — `custom-templates/home.html`

`src/main/resources/custom-templates/home.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Custom ViewResolver Demo</title>
</head>
<body>
    <h1 th:text="${message}">Default message</h1>
</body>
</html>
```

## 11. ขั้นตอนที่ 7 — `.gitignore`

```
target/
.idea/
*.iml
.vscode/
.classpath
.project
.settings/
```

## 12. รันทดสอบ

```bash
mvn spring-boot:run
```

เปิดเบราว์เซอร์ไปที่ `http://localhost:8080/` ควรเห็นข้อความ:

```
Hello from Thymeleaf with a custom ViewResolver!
```

ถ้าไม่เห็น ให้ตรวจสอบว่าไฟล์ `home.html` อยู่ใน `custom-templates/` (ไม่ใช่ `templates/`) และชื่อไฟล์ตรงกับค่าที่ Controller คืนกลับ (`"home"`)

## 13. อัปโหลดขึ้น GitHub

เลือกได้ 2 แบบ แล้วแต่ว่านักศึกษาเก็บงานแล็ปไว้ที่ไหน:

**แบบ A — ทำเป็น repository แยกของตัวเอง**

1. สร้าง repository ใหม่บน GitHub (เช่นชื่อ `spring-thymeleaf-viewresolver-lab`) จะติ๊กหรือไม่ติ๊ก "Add a README file" ก็ได้ — ถ้าติ๊กไว้ ให้ `git pull origin main --allow-unrelated-histories` ก่อน push เพื่อไม่ให้ชนกัน
2. ที่เครื่องของนักศึกษา ในโฟลเดอร์ `spring-thymeleaf-demo/`:

```bash
git init
git add .
git commit -m "Custom ViewResolver lab - CP353002"
git branch -M main
git remote add origin https://github.com/<username>/<repo-name>.git
git push -u origin main
```

**แบบ B — เก็บรวมไว้ใน repository เดียวกับแล็ปอื่นๆ (เช่น `lab/Lab06_spring-thymeleaf-demo`)**

อย่ารัน `git init` ซ้ำข้างในโฟลเดอร์นี้ (จะกลายเป็น repo ซ้อน repo) แค่ก็อปโฟลเดอร์เข้าไปในที่เก็บแล็ปที่มีอยู่แล้ว แล้ว `git add`/`commit`/`push` จาก repo ใหญ่ตามปกติ

```bash
git add lab/Lab06_spring-thymeleaf-demo
git commit -m "Add Lab06 spring-thymeleaf-demo"
git push
```

3. แนบลิงก์ repository (หรือลิงก์โฟลเดอร์ย่อย) ที่ส่งอาจารย์/ผู้ตรวจ

## 14. ต่อยอด (ไม่บังคับ)

- เพิ่ม `@GetMapping("/about")` ตัวใหม่ และไฟล์ `about.html` ใน `custom-templates/` เพื่อดูว่า path resolution ทำงานกับหลาย view อย่างไร
- ลองสร้าง `ViewResolver` ตัวที่สองที่ `order(2)` ชี้ไปโฟลเดอร์อื่น แล้วสังเกตว่า Spring เลือก resolver ตัวไหนก่อน
- เปลี่ยน `setSuffix(".html")` เป็นค่าอื่น แล้วดูว่าเกิด error อะไรเมื่อหาไฟล์ไม่เจอ (เข้าใจ failure mode ของ ViewResolver)

## 15. แบบทดสอบ

คำถามท้ายแล็ปอยู่ในไฟล์แยกต่างหาก: **`ViewResolver_Quiz.docx`** มี 3 ส่วน — ปรนัย, อัตนัย, และภาคปฏิบัติ (แก้โค้ดจริงแล้วแนบภาพหน้าจอ)
ทำเสร็จแล้วแนบไฟล์นี้ (พร้อมภาพหน้าจอ) ไปพร้อมกับลิงก์ repository

## อ้างอิง

- เนื้อหาต้นฉบับ: `springViewResolverConfiguration.ipynb`, `View in Spring Framework.pdf` (Punyphol Horata, College of Computing, KKU)
- [Spring Framework — View Technologies docs](https://docs.spring.io/spring-framework/reference/web/webmvc-view.html)
- [Thymeleaf official site](https://www.thymeleaf.org/)
