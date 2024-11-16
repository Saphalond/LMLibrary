LMLibrary memo
===

Notes on the loading specifications of texture packs and sound packs loaded with LMLibrary    

---

## firis.lmmmパッケージ
 A package that summarizes the base classes of externally read multi-model classes.
Implemented with the aim of eliminating the influence of Minecraft and MOD from the multi-model side and maintaining compatibility between versions.
The original processing was a mixture of Minecraft processing and multi-model processing, so the packages were organized and the processing was remade. 
 - **firis.lmmm.api.caps.IModelCaps**
   - Class for passing parameters from the MOD side to the multi-model side
   - All Entity states are accessed via ModelCaps and values ​​are passed one-way from Entity -> Multi-model side.
 - **firis.lmmm.api.model**
   - A package that summarizes the base classes of multi-models.
   - For standard maid model, inherit "ModelLittleMaidBase" and implement multi-model
   - For non-standard maid models (other than large or humanoid), inherit "ModelMultiBase" and implement multi-model
   - If the class name inherits "ModelMultiBase" and starts with "ModelLittleMaid_" or "ModelMulti_", it will be recognized as a multi-model.
 - **firis.lmmm.api.model.motion.ILMMotion**
   - Interface for implementing standard maid model motion
   - It is assumed that motion processing will only be reflected on multi-models that inherit "ModelLittleMaidBase"
 - **firis.lmmm.api.model.parts**
   - A package containing model parts for multi-models
   - Classes are implemented with Minecraft ModelBox compatibility.
 - **firis.lmmm.api.renderer**
   - A package that summarizes renderer classes for multi-models.
   - The class is implemented with Minecraft ModelRenderer compatibility.
 - **firis.lmmm.builtin.model**
   - Standard maid model implementation class
   - The standard default/SR2/Aug model is implemented.

   
#### 課題
Currently, motion processing can only control body movements.
Most of the eye plashes and cheeks are uniquely implemented in the additional multi-models, so they cannot be handled with standard extensions alone.
  - Plan 1
    - Create a base model that is an extension of "ModelLittleMaidBase" specialized for motion systems.
    - For the uniquely implemented type, only the body movement will be adapted as is.
  - Plan 2
    - Expand "ModelLittleMaidBase" and add eye patter and cheeks
    - If you are implementing your own, you can dynamically pick up multiple patterns (eyeL/EyeL, etc.) using reflection and if things go well, add them to internal variables.
    - Although it is impossible to cover everything, provide patterns of frequently used models
    - **Since it has not been tested, it is necessary to verify whether it can actually be done**

I would like a mechanism to add unique motions, but it is currently difficult.
Even if I add it, I have to consider whether it will be a MOD or a script, so I'll put it on hold.

---

## firis.lmlibパッケージ
A package that implements the basic processing to use multi-model systems in various ways.
Implements the classes required when using Renderer, ModelBase, and maid models to bridge Minecraft and multi-models.
 - **firis.lmlib.api**
   - Defines each implementation class for use from the MOD side
   - **firis.lmlib.api.LMLibraryAPI**
     - Defines API for use from MODs such as multi-model definition class and sound pack acquisition.
   - **firis.lmlib.api.constant**
     - Color information, sound information, etc. are defined using enum constants.
   - **firis.lmlib.api.resource**
     - Object class for managing information on multi-models, sound packs, and multi-model structure classes.
     - Convert the read information into MultiModelPack and TexturePack and generate LMTextureBox from these two pieces of information.
     - Since LMTextureBox has all the information for each texture pack, this definition is mainly used when handling it from the MOD side.
   - **For other implementation classes, write comments in the class file body**
  
For parts other than firis.lmlib.api, loader definitions for texture packs and sound packs, general-purpose multi-model selection screen, etc. are implemented.  
   
---
  
### Loader
Main processing of LMLibrary
Load the texture pack and sound pack and register them to the internal class
Loading is performed using a custom ClassLoader and bytecode conversion is performed using ASM at the timing of findClass.
For details on the reading format, see separate page.
  
---
  
### LMLibrary Use as a prerequisite MOD
LMLibrary The following structure is assumed when drawing a little maid model using
#### MinecraftMOD Each class for use with
  - **LMModelLittleMaid**
    - Wrapper class for handling "ModelMultiBase" as ModelBase
    - For basic usage, there is no need to be aware of it as it is just handled by Render.
  - **LMRenderMultiModel**
    - Class for use as a Renderer
    - To draw LMModelLittleMaid, a class that implements the IModelCompound/IModelCaps interface is required.
  - **IModelCompound**
    - Interface for managing multi-model texture names, color numbers, and contract status
    - Since it is requested by LMRenderMultiModel.getModelConfigCompoundFromEntity, it is necessary to prepare for drawing by receiving from Entity/implementing a fixed class.
    - Since we have implemented ModelCompoundEntityBase that is assumed to be used in Entity, basically use this and extend it as necessary.
  - **IModelCaps**
    - Interface for passing Entity state to multi-model.
    - On the multi-model side, there is no access to Minecraft classes, all access is done via IModelCaps.
    - IModelCaps are obtained via IModelCompound.
    - Since ModelCapsEntityBase is implemented which is assumed to be used in Entity, basically use this and extend it as necessary.
---

#### About other drawings
The little maid model's armor and handheld items are defined as layers.
  - **LMLayerArmorBase**
    - A class that implements the processing necessary to draw an armor model.
    - The basic structure is the same as LMRenderMultiModel, so IModelCompound/IModelCaps interface is required.
  - **LMLayerHeldItemBase**
    - A class that implements the processing necessary to draw the items you have.
    - If you want to use it, you need to implement a method to get drawing items from Entity.
---
  
#### About using the texture selection screen
Can be used by passing a class that implements IGuiTextureSelect to LMLibraryAPI.openGuiTextureSelect
Basically it is assumed to be inherited to Entity, but it can be used in other cases as well.
Since it is implemented only on the client side, synchronization processing after changes must be implemented separately.  


