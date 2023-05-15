package com.saied.binaryvault.appuser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.saied.binaryvault.appuser.dtos.AppUserCreationRequest;
import com.saied.binaryvault.appuser.exceptions.AppUserAlreadyExistsException;
import com.saied.binaryvault.appuser.exceptions.AppUserNotFoundException;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTests {

    @Mock
    private AppUserRepository appUserRepo;
    private AppUserService appUserService;

    @BeforeEach
    public void setUp() {
        appUserService = new AppUserService(appUserRepo);
    }

    @Test
    public void testFindById() {
        Long id = 1L;
        AppUser appUser = new AppUser();
        appUser.setId(id);
        when(appUserRepo.findById(id)).thenReturn(Optional.of(appUser));
        AppUser result = appUserService.findById(id);
        verify(appUserRepo).findById(id);
        assertEquals(appUser, result);
    }

    @Test
    public void testFindByUsername() {
        String username = "testuser";
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        when(appUserRepo.findByUsername(username)).thenReturn(Optional.of(appUser));
        AppUser result = appUserService.findByUsername(username);
        verify(appUserRepo).findByUsername(username);
        assertEquals(appUser, result);
    }

    @Test
    public void testFindByEmail() {
        String email = "test@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        when(appUserRepo.findByEmail(email)).thenReturn(Optional.of(appUser));
        AppUser result = appUserService.findByEmail(email);
        verify(appUserRepo).findByEmail(email);
        assertEquals(appUser, result);
    }

    @Test
    public void testNotFoundById() {
        Long id = 1L;
        when(appUserRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(AppUserNotFoundException.class, () -> appUserService.findById(id));
    }

    @Test
    public void testNotFoundByUsername() {
        String username = "test";
        when(appUserRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(AppUserNotFoundException.class, () -> appUserService.findByUsername(username));
    }

    @Test
    public void testNotFoundByEmail() {
        String email = "test@example.com";
        when(appUserRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(AppUserNotFoundException.class, () -> appUserService.findByEmail(email));
    }

    @Test
    public void testCreateAppUserSuccess() {
        AppUserCreationRequest request = new AppUserCreationRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password");
        when(appUserRepo.selectExistsUsername(request.getUsername())).thenReturn(false);
        when(appUserRepo.selectExistsEmail(request.getEmail())).thenReturn(false);
        ArgumentCaptor<AppUser> userArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        appUserService.createAppUser(request);
        verify(appUserRepo).saveAndFlush(userArgumentCaptor.capture());
        AppUser createdAppUser = userArgumentCaptor.getValue();
        assertEquals(request.getUsername(), createdAppUser.getUsername());
        assertEquals(request.getEmail(), createdAppUser.getEmail());
        assertEquals(request.getFirstName(), createdAppUser.getFirstName());
        assertEquals(request.getLastName(), createdAppUser.getLastName());
        assertEquals(request.getPassword(), createdAppUser.getPassword());
    }

    @Test
    public void testCreateAppUserUsernameExists() {
        AppUserCreationRequest request = new AppUserCreationRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password");
        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setUsername(request.getUsername());
        when(appUserRepo.selectExistsUsername(request.getUsername())).thenReturn(true);
        assertThrows(AppUserAlreadyExistsException.class, () -> appUserService.createAppUser(request));
    }

    @Test
    public void testCreateAppUserEmailExists() {
        AppUserCreationRequest request = new AppUserCreationRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password");
        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setEmail(request.getEmail());
        when(appUserRepo.selectExistsEmail(request.getEmail())).thenReturn(true);
        assertThrows(AppUserAlreadyExistsException.class, () -> appUserService.createAppUser(request));
    }
}
