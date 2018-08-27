package com.chattriggers.ctjs.minecraft.libs

import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

//#if MC<=10809
import net.minecraft.util.IChatComponent
//#else
//$$ import net.minecraft.util.text.ITextComponent
//#endif

//TODO: figure out what is not needed anymore after the kotlin conversion and remove
object EventLib {
    @JvmStatic
    fun getButton(event: MouseEvent): Int {
        return event.button
    }

    @JvmStatic
    fun getButtonState(event: MouseEvent): Boolean {
        //#if MC<=10809
        return event.buttonstate
        //#else
        //$$ return event.isButtonstate()
        //#endif
    }

    @JvmStatic
    fun getType(event: RenderGameOverlayEvent): RenderGameOverlayEvent.ElementType {
        return event.type
    }

    @JvmStatic
    fun getType(event: ClientChatReceivedEvent): Int {
        //#if MC<=10809
        return event.type.toInt()
        //#else
        //$$ return event.type.id.toInt()
        //#endif
    }

    @JvmStatic
    fun getMessage(event: ClientChatReceivedEvent):
            //#if MC<=10809
            IChatComponent {
            //#else
            //$$ ITextComponent {
            //#endif
        return event.message
    }

    @JvmStatic
    fun getName(event: PlaySoundEvent): String {
        return event.name
    }

    @JvmStatic
    fun getModId(event: ConfigChangedEvent.OnConfigChangedEvent): String {
        return event.modID
    }

    /**
     * Cancel an event. Automatically used with `cancel(event)`.
     *
     * @param event the event to cancel
     * @throws IllegalArgumentException if event can be cancelled "normally"
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun cancel(event: Any) {
        when (event) {
            is CallbackInfoReturnable<*> -> {
                if (!event.isCancellable) return
                event.setReturnValue(null)
            }
            is CallbackInfo -> {
                if (!event.isCancellable) return
                event.cancel()
            }
            is PlaySoundEvent ->
                //#if MC<=10809
                event.result = null
                //#else
                //$$ event.resultSound =null;
                //#endif
            else -> throw IllegalArgumentException()
        }
    }
}