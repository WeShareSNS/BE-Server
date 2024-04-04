package com.weshare.api.v1.domain.user;

import java.util.regex.Pattern;

public class ProfileImgValidator {

    private static final Pattern URL_PATTERN = Pattern.compile("^((http|https)://)?(www.)?([a-zA-Z0-9]+)\\.[a-z]+([a-zA-z0-9.?#]+)?$");
    private ProfileImgValidator() {
    }
    static boolean isUrlPattern(String url) {
        return URL_PATTERN.matcher(url).matches();
    }
}
