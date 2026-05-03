package com.bookreview.web;

import java.io.Serializable;

public record LoginMember(Long id, String nickname) implements Serializable {}
