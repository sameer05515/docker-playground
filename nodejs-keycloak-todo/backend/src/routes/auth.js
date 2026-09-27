const express = require("express");
const passport = require("../auth");

const router = express.Router();

router.get("/login", passport.authenticate("keycloak"));

router.get(
  "/callback",
  passport.authenticate("keycloak", {
    failureRedirect: "/?error=login_failed"
  }),
  (req, res) => {
    res.redirect("/todos");
  }
);

router.get("/logout", (req, res, next) => {
  req.logout((err) => {
    if (err) return next(err);

    req.session.destroy(() => {
      const logoutUrl =
        `${process.env.KEYCLOAK_LOGOUT_URL}` +
        `?client_id=${encodeURIComponent(process.env.KEYCLOAK_CLIENT_ID)}` +
        `&post_logout_redirect_uri=${encodeURIComponent("http://localhost:3000/")}`;

      res.redirect(logoutUrl);
    });
  });
});

module.exports = router;
