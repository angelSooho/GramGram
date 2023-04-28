package com.example.mission_leesooho.boundedContext.likeablePerson.controller;

import com.example.mission_leesooho.boundedContext.likeablePerson.dto.form.AddForm;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.form.ModifyForm;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.example.mission_leesooho.global.rq.Rq;
import com.example.mission_leesooho.global.rsData.RsData;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.LikeablePersonResponse;
import com.example.mission_leesooho.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usr/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {

    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like")
    public String showAdd() {
        return "usr/likeablePerson/like";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    public String like(@Valid AddForm addForm) {

        RsData<LikeablePersonResponse> rsData = likeablePersonService.like(rq.getMember(), addForm.username(), addForm.attractiveTypeCode());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        return rq.redirectWithMsg("/usr/likeablePerson/list", rsData);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public String cancel(@PathVariable("id") Long id) {

        RsData<LikeablePersonResponse> deleteRsData = likeablePersonService.cancel(rq.getMember(), id);

        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }

        return rq.redirectWithMsg("usr/likeablePerson/list", deleteRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(@PathVariable Long id, Model model) {

        RsData<LikeablePerson> modifyLike = likeablePersonService.ModifyLike(id, rq.getMember());

        if (modifyLike.isFail()) {
            return rq.historyBack(modifyLike);
        }

        model.addAttribute("likeablePerson", modifyLike.getData());

        return "usr/likeablePerson/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, @Valid ModifyForm modifyForm) {
        RsData<LikeablePerson> rsData = likeablePersonService.modifyAttractiveCode(rq.getMember(), id, modifyForm.attractiveTypeCode());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        return rq.redirectWithMsg("/usr/likeablePerson/list", rsData);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(Model model) {

        model.addAttribute("likeablePeople", likeablePersonService.show(rq.getMember()));

        return "usr/likeablePerson/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/toList")
    @ResponseBody
    public String showToList(Model model) {
        //TODO : showToList 구현해야 함
        return "usr/likeablePerson/toList 구현해야 함";
    }
}
