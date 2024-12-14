package com.example.flo

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import java.util.Date

object JwtUtils {
    private const val SECRET_KEY = "YourSecretKeyForJwt1234567890123456" // 32바이트 이상
    private const val EXPIRATION_TIME = 1000 * 60 * 60 * 24 // 24시간 (1일)

    private val key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())

    // 1. JWT 생성
    fun generateToken(userId: String): String {
        return Jwts.builder()
            .setSubject(userId) // 유저 ID (또는 고유 값)
            .setIssuedAt(Date()) // 생성 시간
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
            .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘
            .compact()
    }

    // 2. JWT 검증 및 Claims 반환
    fun validateToken(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key) // SECRET_KEY로 서명 검증
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: JwtException) {
            null // 검증 실패 시 null 반환
        }
    }

    // 3. 토큰에서 사용자 정보 추출
    fun getUserIdFromToken(token: String): String? {
        val claims = validateToken(token) ?: return null
        return claims.subject // setSubject()에 저장한 값 반환
    }
}
