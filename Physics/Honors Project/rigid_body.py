import pygame
class rect:
    
    def __init__(self,pos,width,height,label):
        self.rect = pygame.Rect(pos[0],pos[1],width,height)
        font = pygame.font.Font('freesansbold.ttf', 32)
        self.label = font.render(label,True, (255,255,255))
        self.stats = None
        self.velocity = 0
        self.mass = 0
        self.acceleration = 0
        self.x = pos[0]

    def setVelocity(self, value):
        #divide by 60 since 60 fps and moves every frame
        self.velocity = value/60
    
    def flip(self):
        self.velocity *= -1

    def setMass(self, value):
        self.mass = value

    def setX(self, value):
        self.x = value
    
    def setAcceleration(self, value):
        self.acceleration = value/60
    
    def updateVelocity(self):
        if(self.velocity == 0):
            return
        if(self.velocity > 0):
            self.velocity -= self.acceleration
            if(self.velocity < 0):
                self.velocity = 0
        else:
            self.velocity += self.acceleration
            if(self.velocity > 0):
                self.velocity = 0

    def update(self):
        self.x += self.velocity
        self.updateVelocity()
        self.rect.x = self.x
        font = pygame.font.Font('freesansbold.ttf', 14)
        self.velocity_label = font.render(f'Velocity:{round(self.velocity*60)}m/s',True, (255,255,255))
        self.mass_label = font.render(f'Mass:{self.mass}kg',True, (255,255,255))
        


    def collide(self, them_initial):
        them_mass = them_initial[0]
        them_velocity = them_initial[1]
        new_velocity = (2*them_mass*them_velocity+self.velocity*(self.mass-them_mass))/(self.mass+them_mass)
        self.velocity = new_velocity

    