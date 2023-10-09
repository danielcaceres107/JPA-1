package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Mauricio
 */
@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    @PersistenceContext(unitName = "CompetitorsPU")
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {
        try {
            entityManager.getTransaction().begin();
            Competitor competitorTmp = new Competitor();
            competitorTmp.setAddress(competitor.getAddress());
            competitorTmp.setAge(competitor.getAge());
            competitorTmp.setCellphone(competitor.getCellphone());
            competitorTmp.setCity(competitor.getCity());
            competitorTmp.setCountry(competitor.getCountry());
            competitorTmp.setName(competitor.getName());
            competitorTmp.setSurname(competitor.getSurname());
            competitorTmp.setTelephone(competitor.getTelephone());
            entityManager.persist(competitorTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(competitorTmp);

            // Crear una respuesta JSON con el ID del competidor creado
            JSONObject rta = new JSONObject();
            rta.put("competitor_id", competitorTmp.getId());

            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return Response.status(500).header("Access-Control-Allow-Origin", "*").entity("Error al crear el competidor").build();
        } finally {
            entityManager.clear();
            entityManager.close();
        }
    }
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            Query query = entityManager.createQuery("SELECT c FROM Competitor c WHERE c.email = :email");
            query.setParameter("email", email);
            Competitor competitor = (Competitor) query.getSingleResult();

            if (competitor != null && competitor.getPassword() != null && competitor.getPassword().equals(password)) {
                
                Map<String, Object> responseMap = new HashMap<String, Object>();
                responseMap.put("id", competitor.getId());
                responseMap.put("name", competitor.getName());
                responseMap.put("surname", competitor.getSurname());
                responseMap.put("age", competitor.getAge());
                responseMap.put("cellphone", competitor.getCellphone());
                responseMap.put("address", competitor.getAddress());
                responseMap.put("city", competitor.getCity());
                responseMap.put("country", competitor.getCountry());
                
                return Response.status(200)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity(responseMap)
                        .build();
            } else {
                throw new NotAuthorizedException("Correo o contraseña invalida");
            }
        } catch (NoResultException e) {
            throw new NotAuthorizedException("Correo o contraseña invalida");
        } catch (Throwable t) {
            t.printStackTrace();
            return Response.status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error en la autenticación")
                    .build();
        }
    }

}