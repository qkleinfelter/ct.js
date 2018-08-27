package com.chattriggers.ctjs.minecraft.objects

import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.ITextComponent
import com.chattriggers.ctjs.utils.kotlin.TextComponentSerializer
import net.minecraft.client.gui.GuiScreenBook
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import kotlin.reflect.full.memberProperties

class Book(bookName: String) {
    var bookScreen: GuiScreenBook? = null
    val book: ItemStack
    val bookData: NBTTagCompound

    init {
        //#if MC<=10809
        book = ItemStack(Items.written_book)
        //#else
        //$$ book = ItemStack(Items.WRITTEN_BOOK);
        //#endif

        bookData = NBTTagCompound()
        bookData["author"] = NBTTagString(Player.getName())
        bookData["title"] = NBTTagString("CT-$bookName")
        bookData["pages"] = NBTTagList()

        book.tagCompound = bookData
    }

    /**
     * Add a page to the book.
     *
     * @param message the entire message for what the page should be
     * @return the current book to allow method chaining
     */
    fun addPage(message: Message): Book {
        val pages = bookData["pages"] as NBTTagList

        pages.appendTag(NBTTagString(
                TextComponentSerializer.componentToJson(
                        message.getChatMessage()
                )
        ))

        updateBookScreen(pages)

        return this
    }

    /**
     * Overloaded method for adding a simple page to the book.
     *
     * @param message a simple string to make the page
     * @return the current book to allow method chaining
     */
    fun addPage(message: String): Book {
        addPage(Message(message))

        return this
    }

    /**
     * Sets a page of the book to the specified message.
     *
     * @param pageNumber the number of the page to set
     * @param message    the message to set the page to
     * @return the current book to allow method chaining
     */
    fun setPage(pageNumber: Int, message: Message): Book {
        val pages = bookData.getTag("pages") as NBTTagList

        pages.set(pageNumber, NBTTagString(
                TextComponentSerializer.componentToJson(
                        message.getChatMessage()
                )
        ))

        updateBookScreen(pages)

        return this
    }

    fun updateBookScreen(pages: NBTTagList) {
        bookData.removeTag("pages")
        bookData["pages"] = pages
        book.tagCompound = bookData

        if (bookScreen != null) {
            ReflectionHelper.setPrivateValue<GuiScreenBook, NBTTagList>(
                    GuiScreenBook::class.java,
                    bookScreen, pages,
                    "field_146483_y",
                    "bookPages"
            )
        }
    }

    @JvmOverloads
    fun display(page: Int = 0) {
        if (bookScreen == null) {
            bookScreen = GuiScreenBook(Player.getPlayer(), book, false)
        }

        ReflectionHelper.setPrivateValue<GuiScreenBook, Int>(
                GuiScreenBook::class.java,
                bookScreen,
                page,
                "currPage",
                "field_146484_x"
        )

        GuiHandler.openGui(bookScreen ?: return)
    }

    fun isOpen(): Boolean {
        return Client.getMinecraft().currentScreen === bookScreen
    }

    fun getCurrentPage(): Int {
        return if (!isOpen()) -1 else ReflectionHelper.getPrivateValue<Int, GuiScreenBook>(GuiScreenBook::class.java, bookScreen, "currPage", "field_146484_x")
    }

    operator fun NBTTagCompound.set(tag: String, value: NBTBase) {
        this.setTag(tag, value)
    }

    operator fun NBTTagCompound.get(tag: String): NBTBase {
        return this.getTag(tag)
    }
}