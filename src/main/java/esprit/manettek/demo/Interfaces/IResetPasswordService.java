package esprit.manettek.demo.Interfaces;

import esprit.manettek.demo.Entities.ResetPassword;
import esprit.manettek.demo.Entities.User;

public interface IResetPasswordService {

    public void create(ResetPassword entity);
    public ResetPassword get(User user);

    public void ResetPassword(User user);
}
