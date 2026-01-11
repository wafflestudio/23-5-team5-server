package com.team5.studygroup

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing // 1. 엔티티의 생성/수정 시간을 자동으로 기록하기 위해 필수!
@EnableJpaRepositories // 2. JPA 리포지토리(UserRepository 인터페이스)를 활성화
@SpringBootApplication
class StudygroupApplication

fun main(args: Array<String>) {
    runApplication<StudygroupApplication>(*args)
}
