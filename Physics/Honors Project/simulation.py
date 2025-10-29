import pygame
import rigid_body
import input_box

def draw(sprite,screen):
    pygame.draw.rect(screen, (255,255,255),sprite.rect,2)

    label_rect = sprite.label.get_rect()
    label_rect.center = sprite.rect.center
    screen.blit(sprite.label,label_rect)
    

def drawSprites(sprites,screen):
    for sprite in sprites:
        draw(sprite,screen)

def updateSprites(sprites):
    for sprite in sprites:
        sprite.update()

def checkBoundries(sprite, screen):
    if(sprite.rect.right > pygame.display.get_window_size()[0]):
        sprite.flip()
        sprite.x = pygame.display.get_window_size()[0] - sprite.rect.width
    if(sprite.rect.left < 0):
        sprite.flip()
        sprite.x = 0


def checkCollisons(sprites,screen):
    if(pygame.Rect.colliderect(sprites[0].rect,sprites[1].rect)):
        handleCollision(sprites[0],sprites[1])
    for sprite in sprites:
        checkBoundries(sprite,screen)

def handleCollision(a,b):
    a_initial = (a.mass,a.velocity)
    b_initial = (b.mass,b.velocity)
    print(f'initial KE:{1/2*a.mass*a.velocity*a.velocity+1/2*b.mass*b.velocity*b.velocity}')
    a.collide(b_initial)
    b.collide(a_initial)
    print(f'finalKE:{1/2*a.mass*a.velocity*a.velocity+1/2*b.mass*b.velocity*b.velocity}')
    print(f'v1i+v1f:{a_initial[1]+a.velocity}')
    print(f'v2i+v2f:{b_initial[1]+b.velocity}')
    a.rect.right = b.rect.left - 5

def drawLabels(sprites,screen):
    for sprite in sprites:
        drawLabel(sprite)

def drawLabel(sprite):
    velocity_rect = sprite.velocity_label.get_rect()
    velocity_rect.bottom = sprite.rect.top - 10
    velocity_rect.centerx = sprite.rect.centerx
    screen.blit(sprite.velocity_label,velocity_rect)

    mass_rect = sprite.mass_label.get_rect()
    mass_rect.bottom = velocity_rect.top
    mass_rect.centerx = velocity_rect.centerx
    screen.blit(sprite.mass_label,mass_rect)

def drawInputs(boxes,screen):
    for box in boxes:
        drawInput(box,screen)

def drawInput(box,screen):
    pygame.draw.rect(screen,box.color,box.rect)
    font = pygame.font.Font('freesansbold.ttf', 32)
    text_surface = font.render(box.text, True, (0,0,0)) 
    screen.blit(text_surface, (box.rect.x+5, box.rect.y+5)) 
    box.rect.w = max(100, text_surface.get_width()+10)
    label = font.render(box.label, True, (255,255,255))
    label_rect = label.get_rect()
    label_rect.right = box.rect.left - 10
    label_rect.top = box.rect.top
    screen.blit(label,label_rect)

def verify(string):
    if(string == ''):
        return 200
    else:
        return string
    
def verifyFriction(string):
    if(string == ''):
        return 0
    else:
        return string
def verifyGravity(string):
    if(string == ''):
        return 9.8
    else:
        return string

cur_color = (255,255,0)
def get_color(time):
    global cur_color
    if(time%60 != 0):
        return cur_color
    if(cur_color == (255,255,0)):
        cur_color = (255,255,255)
    else:
        cur_color = (255,255,0)
    return cur_color

def drawInstructions(screen,mode,time):
    font = pygame.font.Font('freesansbold.ttf', 15)
    color = get_color(time)
    text_surface = font.render(f"Press Enter To {mode} Simulation", True, color)
    rect = text_surface.get_rect()
    rect.top = 100
    rect.centerx =  pygame.display.get_window_size()[0]/2
    screen.blit(text_surface,rect)

def clearBoxes(boxes):
    for box in boxes:
        box.clear()

def calcAcc(gravity,friction):
    acceleration = gravity*friction
    return acceleration

pygame.init()

screen_width = 800
screen_height = 600
screen = pygame.display.set_mode((screen_width, screen_height))
pygame.display.set_caption("Collision Simulator")

clock = pygame.time.Clock()

sprites = []

sprite_size = 150

sprites.append(rigid_body.rect((0,screen_height-sprite_size),sprite_size,sprite_size,"1"))

sprites.append(rigid_body.rect((screen_width-sprite_size,screen_height-sprite_size),sprite_size,sprite_size,"2"))

input_boxes = []
input_boxes.append(input_box.input((80,50),"m1"))
input_boxes.append(input_box.input((80,150),"v1"))

input_boxes.append(input_box.input((screen_width-140,50),"m2"))
input_boxes.append(input_box.input((screen_width-140,150),"v2"))

input_boxes.append(input_box.input((screen_width/2+20,160),"Coeff KF"))
input_boxes.append(input_box.input((screen_width/2+20,210),"Gravity"))

time = 0
# Game loop
running = True
simulation_running = False
while running:
    # Handle events
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        if event.type == pygame.MOUSEBUTTONDOWN:
            for box in input_boxes:
                box.checkClicked(event)
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_RETURN:
                if(not simulation_running):
                    simulation_running = True
                    coeffKF = float(verifyFriction(input_boxes[4].text))
                    gravity  = float(verifyGravity(input_boxes[5].text))
                    acceleration = calcAcc(gravity,coeffKF)
                    sprites[0].setMass(int(verify(input_boxes[0].text)))
                    sprites[0].setVelocity(float(verify(input_boxes[1].text)))
                    sprites[0].setAcceleration(acceleration)
                    sprites[1].setMass(int(verify(input_boxes[2].text)))
                    sprites[1].setVelocity(float(verify(input_boxes[3].text)))
                    sprites[1].setAcceleration(acceleration)
                else:
                    simulation_running = False
                    sprites[0].setX(0)
                    sprites[1].setX(screen_width-sprite_size)
                    sprites[0].setVelocity(0)
                    sprites[1].setVelocity(0)
                    updateSprites(sprites)
                    clearBoxes(input_boxes)
            for box in input_boxes:
                box.addText(event)


    screen.fill((0, 0, 0))  # Clear the screen
    time+=1
    if(simulation_running):
        checkCollisons(sprites,screen)
        updateSprites(sprites)
        drawLabels(sprites,screen)
        drawInstructions(screen,"Stop",time)
    else:
        drawInstructions(screen,"Start",time)
        drawInputs(input_boxes,screen)

    drawSprites(sprites,screen)

    # Update the display
    pygame.display.flip()
    clock.tick(60) # Limit to 60 FPS

# Quit Pygame
pygame.quit()