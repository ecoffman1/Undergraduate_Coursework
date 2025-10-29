import pygame

class input:
    def __init__(self,pos,label):
        self.label = label
        self.active = False
        self.text = ""
        self.rect = pygame.Rect(pos[0], pos[1], 140, 32) 
        self.color = (255,255,255)
        

    def checkClicked(self,event):
        if self.rect.collidepoint(event.pos):
            self.active = True
            self.color = pygame.Color('grey')
        else:
            self.color = (255,255,255)
            self.active = False

    def addText(self,event):
        if(not self.active):
            return
        if event.key == pygame.K_BACKSPACE: 
            self.text = self.text[:-1] 
        else: 
            char = event.unicode
            if(char.isnumeric() or char=="."):
                self.text += event.unicode

    def clear(self):
        self.text = ""
        self.active = False
        self.color = (255,255,255)