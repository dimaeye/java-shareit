package ru.practicum.shareit.user.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstant {
    public static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
}
