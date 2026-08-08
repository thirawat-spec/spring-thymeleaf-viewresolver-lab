package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "สวัสดีครับผมชื่อ นายถิรวัฒน์ อุจินา");
        model.addAttribute("studentId", "673380039-7");
        return "home"; // ไม่ใช่ path ไฟล์ แค่ "ชื่อ view" เชิงตรรกะเท่านั้น
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("intro", "ผมชื่อฟิล์ม เป็นนักศึกษาสาขา CS ปี 3 มหาวิทยาลัยขอนแก่น");
        return "about";
    }
}