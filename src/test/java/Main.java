import dev.getelements.elements.sdk.dao.UserDao;
import dev.getelements.elements.sdk.local.ElementsLocalBuilder;
import dev.getelements.elements.sdk.model.user.User;

/**
 * Local development entry point for running the presale proxy Element.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        final var local = ElementsLocalBuilder.getDefault()
                .withElementNamed("server", "zyx.oncade.element")
                .build();

        final var dao = local.getRootElementRegistry()
                .find("dev.getelements.elements.sdk.dao")
                .findFirst()
                .get();

        final var userDao = dao
                .getServiceLocator()
                .getInstance(UserDao.class);

        final var user = new User();
        user.setName("admin");
        user.setEmail("hi@oncade.xyz");
        user.setLevel(User.Level.SUPERUSER);
        userDao.createUserWithPassword(user, "passwd");

        try (local) {
            local.start();
            local.run();
        }
    }
}