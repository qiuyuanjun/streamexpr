package com.qiuyj.streamexpr.api;

import com.qiuyj.streamexpr.api.utils.StringUtils;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qiuyj
 * @since 2023-07-03
 */
public class TokenKinds {

    private static volatile TokenKinds instance;

    public static TokenKinds getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (TokenKinds.class) {
                if (Objects.isNull(instance)) {
                    instance = new TokenKinds();
                }
            }
        }
        return instance;
    }

    private final AtomicBoolean initInternal = new AtomicBoolean();

    public void initInternal() {
        if (initInternal.compareAndSet(false, true)) {
            TokenKindImpl.valueOf("IDENTIFIER");
        }
    }

    private final ConcurrentMap<String, TokenKind> tokenKindNames = new ConcurrentHashMap<>(64);

    private final ConcurrentMap<Integer, TokenKind> tokenKindTags = new ConcurrentHashMap<>(64);

    private final ConcurrentMap<String, TokenKind> keywords = new ConcurrentHashMap<>();

    public void registerTokenKind(TokenKind tokenKind) {
        Objects.requireNonNull(tokenKind);
        String name = tokenKind.getName();
        if (StringUtils.isNotEmpty(name)
                && Objects.nonNull(tokenKindNames.putIfAbsent(name, tokenKind))) {
            throw new IllegalStateException("A TokenKind with the current name '" + name + "' already exists");
        }
        int tag = tokenKind.getTag();
        if (tag != TokenKind.TAG_NOT_SUPPORT
                && Objects.nonNull(tokenKindTags.putIfAbsent(tag, tokenKind))) {
            throw new IllegalStateException("A TokenKind with the current tag '" + tag + "' already exists");
        }
        if (tokenKind.isKeyword()
                && Objects.nonNull(keywords.putIfAbsent(name, tokenKind))) {
            throw new IllegalStateException("A TokenKind that is keyword with the current name '" + name + "' already exists");
        }
    }

    public TokenKind getTokenKindByName(String name) {
        return Objects.requireNonNull(tokenKindNames.get(name), () -> "Can not find TokenKind with name: " + name);
    }

    public TokenKind getTokenKindByTag(int tag) {
        return Objects.requireNonNull(tokenKindTags.get(tag), () -> "Can not find TokenKind with tag: " + tag);
    }

    public TokenKind getKeyword(String name) {
        return keywords.get(name);
    }
}
