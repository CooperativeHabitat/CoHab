package by.magofrays.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {

}
