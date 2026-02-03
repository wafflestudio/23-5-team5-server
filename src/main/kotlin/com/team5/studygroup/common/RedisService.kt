package com.team5.studygroup.common

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(
    private val redisTemplate: StringRedisTemplate,
) {
    // 데이터 저장 (유효시간 설정)
    fun setDataExpire(
        key: String,
        value: String,
        duration: Long,
    ) {
        val expireDuration = Duration.ofSeconds(duration)
        redisTemplate.opsForValue().set(key, value, expireDuration)
    }

    fun setBlackList(key: String, value: String, durationMillis: Long) {
        val expireDuration = Duration.ofMillis(durationMillis)
        redisTemplate.opsForValue().set(key, value, expireDuration)
    }

    // 데이터 조회
    fun getData(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    // 데이터 삭제
    fun deleteData(key: String) {
        redisTemplate.delete(key)
    }

    fun isBlacklisted(token: String): Boolean {
        return redisTemplate.hasKey(token)
    }
}
