package com.example.mission_leesooho.boundedContext.notification.controller;

import com.example.mission_leesooho.boundedContext.notification.entity.Notification;
import com.example.mission_leesooho.boundedContext.notification.service.NotificationService;
import com.example.mission_leesooho.global.rq.Rq;
import com.example.mission_leesooho.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final Rq rq;
    private final NotificationService notificationService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list") // page 5 size
    public String showList(Model model, @RequestParam(value = "page", defaultValue="0") int offset) {

        if (!rq.getMember().hasConnectedInstaMember()) {
            RsData<Object>of = RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
            return rq.historyBack(of);
        }
        Page<Notification> notifications = notificationService.findMyNotification(rq.getMember().getInstaMember(), offset);
        model.addAttribute("notifications", notifications);

        return "usr/notification/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String deleteNotification(@RequestParam("id") Long id) {
        notificationService.delete(id);
        return "redirect:/usr/notification/list?page=0";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete-list")
    public String deleteAll() {
        notificationService.deleteAll(rq.getMember().getInstaMember().getId());
        return "redirect:/usr/notification/list";
    }
}
