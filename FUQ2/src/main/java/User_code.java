import login.User_valid;
import DB_entities.Users;

import java.io.Serializable;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceException;
import javax.persistence.SynchronizationType;

/**
 *
 * @author Paulina
 */
@Named
@ConversationScoped
@Stateful
public class User_code implements Serializable
{
    private static final String PAGE_INDEX          = "index?faces-redirect=true";
    private static final String PAGE_CONFIRM        = "confirm?faces-redirect=true";
    private static final long serialVersionUID = 1L;

    @PersistenceContext(type = PersistenceContextType.EXTENDED, synchronization = SynchronizationType.UNSYNCHRONIZED)
    private EntityManager em;

    @Inject
    private User_valid user_valid;
    
    @Inject
    private Conversation conversation;
    
    public Conversation getConversation() {
        return conversation;
    }
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
    
    private Users user = new Users();
    public Users getUser() {
        return user;
    }
    public void setUser(Users user) {
        this.user = user;
    }

    String passRepeat;
    public String getPassRepeat() {
        return passRepeat;
    }
    public void setPassRepeat(String passRepeat) {
        this.passRepeat = passRepeat;
    }
    String genderValue;
    public String getGenderValue() {
        return genderValue;
    }
    public void setGenderValue(String genderValue) {
        this.genderValue = genderValue;
    }

    public String createUser() {

        if(!user_valid.comparePasswords(user.getPassword(), passRepeat))
        {
            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nesutampa slaptazodziai", "Nesutampa slaptazodis"));
            return null;
        }
        else
        {
            if (!conversation.isTransient()) {
            conversation.end();
            return PAGE_INDEX;
            }

            conversation.begin();

            if (conversation.isTransient()) {
                return PAGE_INDEX;
            }

            user.setSex(user_valid.convertGender(genderValue));
            user_valid.create(user);
            return PAGE_CONFIRM;
        }
    }

    public String ok() {
        try {
            conversation.end();
            em.joinTransaction();
            em.flush();
            return PAGE_INDEX;
        } catch (OptimisticLockException ole) {
            // Kažkas kitas buvo greitesnis...
            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Vartotojas nesukurtas. Griskite atgal ir bandykite dar karta.", " "));
            return null;
        } catch (PersistenceException pe) {
            // Kitokios bėdos su DB
            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Vartotojas nesukurtas. Griskite atgal ir bandykite dar karta.", " ")
            );
            return null;
        }
    }

    public String cancel() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
        return PAGE_INDEX;
    }

}