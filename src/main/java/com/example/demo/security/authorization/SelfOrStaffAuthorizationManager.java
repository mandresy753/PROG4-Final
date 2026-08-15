package com.example.demo.security.authorization;

import com.example.demo.entity.JUser;
import com.example.demo.enums.UserRole;
import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/**
 * Regle d'autorisation reutilisable : le staff (TEACHER/ADMIN) a toujours acces, un STUDENT
 * uniquement s'il consulte ses propres donnees, identifiees par le parametre de requete {@code
 * studentId}.
 *
 * <p>Branchee directement dans {@link com.example.demo.security.SecurityConfig} via {@code
 * .access(...)} sur les routes concernees (consultation des rattachements, consultation des notes)
 * : les controllers n'ont plus aucune annotation de securite, toute la regle vit ici. Ca ne
 * fonctionne que parce que {@code studentId} est un parametre de requete (query param), donc deja
 * disponible sur la {@code HttpServletRequest} a ce stade du filtre -- ce ne serait pas possible
 * pour une donnee situee dans le corps JSON (voir {@link
 * com.example.demo.security.GradeAuthorizationService} pour ce cas).
 */
@Component
public class SelfOrStaffAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
    var authentication = authenticationSupplier.get();
    if (!(authentication.getPrincipal() instanceof JUser me)) {
      return new AuthorizationDecision(false);
    }

    if (me.getRole() != UserRole.STUDENT) {
      return new AuthorizationDecision(true);
    }

    var studentId = context.getRequest().getParameter("studentId");
    var granted = studentId != null && studentId.equals(me.getId().toString());
    return new AuthorizationDecision(granted);
  }
}
