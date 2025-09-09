package com.koob.Koob_backend.oAuth2User;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.user.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // fetch user profile from Google
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Extract Google profile fields
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String pictureUrl = (String) attributes.get("picture");
        String givenName = (String) attributes.get("given_name");
        String familyName = (String) attributes.get("family_name");
        String locale = (String) attributes.get("locale");

        // Persist in database
        User user = userService.getOrCreateUser(googleId, email, name, pictureUrl);
        user.setGivenName(givenName);
        user.setFamilyName(familyName);
        user.setLocale(locale);
        userService.updateUser(user);

        // Wrap in custom principal (so downstream we can access `User`)
        return new CustomOAuth2User(oAuth2User, user);
    }
}
