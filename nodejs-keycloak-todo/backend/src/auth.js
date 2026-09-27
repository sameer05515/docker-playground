const passport = require("passport");
const OpenIDConnectStrategy = require("passport-openidconnect");

const strategy = new OpenIDConnectStrategy(
  {
    issuer: process.env.KEYCLOAK_ISSUER,
    authorizationURL: process.env.KEYCLOAK_AUTHORIZATION_URL,
    tokenURL: process.env.KEYCLOAK_TOKEN_URL,
    userInfoURL: process.env.KEYCLOAK_USERINFO_URL,
    clientID: process.env.KEYCLOAK_CLIENT_ID,
    clientSecret: process.env.KEYCLOAK_CLIENT_SECRET,
    callbackURL: process.env.KEYCLOAK_CALLBACK_URL,
    scope: ["openid", "profile", "email"]
  },
  (issuer, profile, done) => {
    // Keycloak's subject is the stable user identifier.
    const user = {
      id: profile.id,
      username: profile.username || profile.displayName || profile.id,
      displayName: profile.displayName || profile.username || profile.id,
      email: profile.emails?.[0]?.value || null
    };

    return done(null, user);
  }
);

passport.use("keycloak", strategy);

passport.serializeUser((user, done) => done(null, user));
passport.deserializeUser((user, done) => done(null, user));

module.exports = passport;
