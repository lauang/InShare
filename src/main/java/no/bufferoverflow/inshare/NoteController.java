package no.bufferoverflow.inshare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import java.util.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import no.bufferoverflow.inshare.Note.Permission;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

/**
 * Controller for handling note operations, such as viewing, editing,
 * creating, deleting, and sharing notes. It integrates with the database using
 * {@link JdbcTemplate} and manages user-specific permissions.
 */
@Controller
@RequestMapping("/note")
public class NoteController {


    /** Template for executing SQL queries against the database. */
    private final JdbcTemplate jdbcTemplate;
    /** Service for loading user details and authentication. */
    private final InShareUserDetailService userDetailService;

    public NoteController(JdbcTemplate jdbcTemplate, InShareUserDetailService userDetailService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDetailService = userDetailService;
    }

    /**
     * Show the view page for a note
     *
     * @param id the unique identifier of the note.
     * @param model the UI model which will be passed to the template. Modified by this method.
     * @return the name of the template ("viewNote")
     */
    @GetMapping("/view/{id}")
    public String showViewForm(@PathVariable("id") UUID id, Model model) {
        Note note = Note.load(jdbcTemplate, id);
        model.addAttribute("note", note);

        if (checkPermission(id, Permission.READ)) {
            return "viewNote"; // Check if user has read permission before displaying note
        } else {
            return "redirect:/"; // Redirect to dashboard if user does not have read permission
        }
    }
    
    /**
     * Displays the form to edit an existing note.
     *
     * @param id the unique identifier of the note.
     * @param model the UI model which will be passed to the template. Modified by this method.
     * @return the view name for editing the note, "editNote"
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") UUID id, Model model) {
        Note note = Note.load(jdbcTemplate, id);
        model.addAttribute("note", note);
        if (checkPermission(id, Permission.WRITE)) { // Check if user has write permission before displaying edit form
            return "editNote";
        } else { // Redirect to dashboard if user does not have write permission
            return "redirect:/"; 
        }
    }

    /**
     * Handles the submission of the edit form and updates the note in the database.
     * This operation is transactional to ensure the note is updated atomically.
     *
     * @param id the unique identifier of the note to be updated.
     * @param name the new name for the note.
     * @param content the new content for the note.
     * @return a redirect to the dashboard after the update.
     */
    @PostMapping("/edit/{id}")
    @Transactional
    public String updateNote(@PathVariable("id") UUID id,
                             @RequestParam("name") String name,
                             @RequestParam("content") String content) {
        Note note = Note.load(jdbcTemplate, id)
                        .withName(name)
                        .withContent(content);

        if (checkPermission(id, Permission.WRITE)) note.save(jdbcTemplate); // Check if user has write permission before saving changes
        return "redirect:/"; // Redirect to dashboard after update
    }


    /**
     * Handles the creation of a new note and assigns default permissions to the
     * authenticated user. This operation is transactional to ensure the note
     * creation and permission assignment are atomic.
     *
     * @param name the name of the new note.
     * @param content the content of the new note.
     * @return a redirect to the edit view of the newly created note.
     */
    @PostMapping("/create")
    @Transactional
    public String createNote(@RequestParam("name") String name,
                             @RequestParam("content") String content) {



        final Authentication authentication
            = SecurityContextHolder.getContext()
                                   .getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && (authentication.getPrincipal() instanceof User)) {
            final User user = (User)authentication.getPrincipal();
            final Note newNote = new Note(user, name, content)
                                .withUserRole(user, Note.Role.OWNER);
                                
            newNote.save(jdbcTemplate);
            return "redirect:/note/edit/" + newNote.id.toString();
        }
        return "redirect:/";
    }

    
    /**
     * Deletes the specified note if the authenticated user has the DELETE permission.
     * This operation is transactional to ensure the note
     * deletion is performed atomically.
     *
     * @param id the unique identifier of the note to be deleted.
     * @return a redirect to the dashboard after deletion.
     */
    @DeleteMapping("/delete/{id}")
    @Transactional
    public String deleteNote(@PathVariable("id") UUID id) { //can be shortened with check permission method
        if (checkPermission(id, Permission.DELETE)) {
            final String deleteNote = "DELETE FROM Note WHERE id = ?";
            jdbcTemplate.update(deleteNote, id.toString());
        }
        return "redirect:/";
    }


    /**
     * Displays the form to share a note with another user.
     *
     * @param id the unique identifier of the note.
     * @param model the model to which the note is added.
     * @return the view name for sharing the note.
     */
    @GetMapping("/share/{id}")
    public String showShareForm(@PathVariable("id") UUID id, Model model) {
        Note note = Note.load(jdbcTemplate, id);
        boolean isOwner = checkRole(id, Note.Role.OWNER);

        model.addAttribute("note", note);
        model.addAttribute("isOwner", isOwner);
        
        if (checkRole(id, Note.Role.OWNER) || checkRole(id, Note.Role.ADMINISTRATOR)) {
            return "shareNote";
        } else {
            return "redirect:/"; // Redirect to dashboard if user does not have permission to share
        }
    }

    /**
     * Retrieves the permissions associated with the specified note for the
     * authenticated user.
     *
     * @param id the unique identifier of the note.
     * @return a map containing the permissions of the authenticated user for the note.
     */
    @GetMapping("/permissions/{id}") 
    @ResponseBody
    public Map<String, Object> getNotePermissions(@PathVariable("id") UUID id) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Set<Note.Permission> permissions = Note.loadPermissions(jdbcTemplate, id)
                .get(user.id)
                .getOrElse(HashSet.of());
        return HashMap.of("permissions", permissions.toJavaSet());
    } //change to getNoteRole 


    /**
     * Shares the specified note with another user and grants them the specified permissions.
     * This operation is transactional to ensure the permissions are added atomically.
     *
     * @param noteId the unique identifier of the note to be shared.
     * @param username the username of the user with whom the note is shared.
     * @param permissions the list of permissions to be granted.
     * @return a redirect to the dashboard after sharing.
     * @throws UsernameNotFoundException if the specified user is not found.
     */
    @PostMapping("/share")
    @Transactional
    public String shareNote(
            @RequestParam UUID noteId,
            @RequestParam String username,
            @RequestParam Note.Role role) {

        // Load the note
        Note note = Note.load(jdbcTemplate, noteId);
        
        // Load the user
        User user = (User)userDetailService.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        //the issuer
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User issuer = (User) authentication.getPrincipal();
        if (user.id.equals(issuer.id)) { //cannot share with self
            return "redirect:/";
        }

        // check role of the issuer
        Note.Role issuerRole = note.userRoles.get(issuer.id).getOrElse(Note.Role.READER);
        if (!issuerRole.equals(Note.Role.OWNER) && !issuerRole.equals(Note.Role.ADMINISTRATOR)) {
            return "redirect:/"; //not allowed to share
        }

        if (role.equals(Note.Role.OWNER)) { // only owner can transfer ownership
            if (issuerRole.equals(Note.Role.OWNER)) {
                note = note.withUserRole(issuer, Note.Role.ADMINISTRATOR); //revoke owner role
            } else {
                return "redirect:/";
            }
        }
        
        note = note.withUserRole(user, role);
        note.save(jdbcTemplate);
        return "redirect:/";
    }

    private boolean checkRole(UUID noteId, Note.Role role) {
        final Authentication authentication
            = SecurityContextHolder.getContext()
                                   .getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && (authentication.getPrincipal() instanceof User)) {
            final User user = (User)authentication.getPrincipal();
            Note note = Note.load(jdbcTemplate, noteId);
            return note.userRoles.get(user.id).getOrElse(Note.Role.READER).equals(role);
        } else return false;
    }


    private boolean checkPermission(UUID noteId, Permission permission) {
        final Authentication authentication
            = SecurityContextHolder.getContext()
                                   .getAuthentication();

        if ( authentication != null
                && authentication.isAuthenticated()
                && (authentication.getPrincipal() instanceof User)) {
            final User user = (User)authentication.getPrincipal();
            Note note = Note.load(jdbcTemplate, noteId);
            Map<UUID, Set<Permission>> permissions = Note.loadPermissions(jdbcTemplate, noteId);
            return permissions.get(user.id).getOrElse(HashSet.of()).contains(permission);
            //return note.userPermissions.get(user.id).isDefined() && note.userPermissions.get(user.id).get().contains(permission);

        } else return false;
    }
}
