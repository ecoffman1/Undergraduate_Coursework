import pygame
# Set up the display
screen_width = 800
screen_height = 600
screen = pygame.display.set_mode((screen_width, screen_height))
pygame.display.set_caption("Pygame Window")

# Game variables
running = True
clock = pygame.time.Clock()

rect = pygame.Rect(30, 30, 60, 60)
color = (255,0,0)
# Main game loop
while running:
    # Event handling
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    # Game logic
    # -- update game state here --
    # Initializing Color
 
    
    rect.right += 1
    # Drawing
    screen.fill((0, 0, 0))  # Fill the screen with black

    #Move Rect right one pixel 
    rect.right += 1

    #  Drawing Rectangle 
    pygame.draw.rect(screen, color, rect)
    # Update the display
    pygame.display.flip()

    # Control the frame rate
    clock.tick(60)  # Limit to 60 frames per second

# Quit Pygame
pygame.quit()