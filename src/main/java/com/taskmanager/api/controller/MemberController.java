package com.taskmanager.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.api.entity.ProjectMember;
import com.taskmanager.api.repository.ProjectMemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final ProjectMemberRepository memberRepository;

    @GetMapping("/project/{projectId}")
    public List<ProjectMember> getMembers(@PathVariable Long projectId) {
        return memberRepository.findByProjectId(projectId);
    }

    @PostMapping
    public ResponseEntity<ProjectMember> addMember(@RequestBody ProjectMember member) {
        return ResponseEntity.ok(memberRepository.save(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMember(@PathVariable Long id) {
        memberRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}