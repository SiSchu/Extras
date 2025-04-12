package net.eloxad.extras.guis;

import com.google.inject.Inject;
import lombok.Getter;
import net.eloxad.extras.interfaces.GUI;
import net.eloxad.extras.managers.CustomGUIManager;
import net.eloxad.extras.utils.CustomGUIHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeGUI implements GUI {
    private final CustomGUIManager guiManager;

    @Inject
    public RecipeGUI(CustomGUIManager guiManager) {
        this.guiManager = guiManager;
        createMainGUI();
    }

    private Map<UUID, PlayerInfo> playerData = new HashMap<>();


    private void createMainGUI() {
        Inventory mainInventory = guiManager.createGUI("recipe_gui", "Select Recipe", 9);
        ItemStack[] items = {ItemStack.of(Material.DIAMOND_AXE), ItemStack.of(Material.DIAMOND_PICKAXE)};
        for (int i = 0; i < items.length; i++) {
            guiManager.addItem(mainInventory, items[i], i, this::removeItem);
        }
        guiManager.registerGUI("recipe_gui", mainInventory);
        guiManager.addBottomInventoryHandler("recipe_gui", this::handlePlayerClick);
    }

    private void handlePlayerClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        Inventory topinv = event.getView().getTopInventory();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) {
            List<List<ItemStack>> allRecipes = new ArrayList<>();
            List<Recipe> recipes = Bukkit.getServer().getRecipesFor(clickedItem);
            event.setCancelled(true);

            if (!recipes.isEmpty()) {
                for (Recipe recipe : recipes) {
                    List<List<ItemStack>> recipesList = new ArrayList<>();

                    if (recipe instanceof ShapedRecipe shapedRecipe) {
                        String[] shape = shapedRecipe.getShape();
                        Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();

                        List<List<ItemStack>> slotOptions = new ArrayList<>(Collections.nCopies(9, null));
                        for (int i = 0; i < 9; i++) {
                            List<ItemStack> defaultSlot = new ArrayList<>();
                            defaultSlot.add(new ItemStack(Material.AIR));
                            slotOptions.set(i, defaultSlot);
                        }

                        for (int row = 0; row < shape.length; row++) {
                            String line = shape[row];
                            for (int col = 0; col < line.length(); col++) {
                                char key = line.charAt(col);
                                int slot = row * 3 + col;
                                List<ItemStack> options = new ArrayList<>();

                                if (key == ' ' || !choiceMap.containsKey(key)) {
                                    options.add(new ItemStack(Material.AIR));
                                } else {
                                    RecipeChoice choice = choiceMap.get(key);
                                    if (choice instanceof RecipeChoice.MaterialChoice matChoice) {
                                        for (Material m : matChoice.getChoices()) {
                                            options.add(new ItemStack(m));
                                        }
                                    } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                                        for (ItemStack stack : exactChoice.getChoices()) {
                                            options.add(stack.clone());
                                        }
                                    } else {
                                        options.add(new ItemStack(Material.AIR));
                                    }
                                }

                                slotOptions.set(slot, options);
                            }
                        }

                        recipesList = generateAllCombinations(slotOptions);

                    } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                        List<RecipeChoice> choices = shapelessRecipe.getChoiceList();
                        List<List<ItemStack>> slotOptions = new ArrayList<>();

                        for (int i = 0; i < 9; i++) {
                            List<ItemStack> options = new ArrayList<>();
                            if (i < choices.size()) {
                                RecipeChoice choice = choices.get(i);
                                if (choice instanceof RecipeChoice.MaterialChoice matChoice) {
                                    for (Material m : matChoice.getChoices()) {
                                        options.add(new ItemStack(m));
                                    }
                                } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                                    for (ItemStack stack : exactChoice.getChoices()) {
                                        options.add(stack.clone());
                                    }
                                } else {
                                    options.add(new ItemStack(Material.AIR));
                                }
                            } else {
                                options.add(new ItemStack(Material.AIR));
                            }
                            slotOptions.add(options);
                        }
                        recipesList = generateAllCombinations(slotOptions);
                    }
                    allRecipes.addAll(recipesList);
                }
                if (!allRecipes.isEmpty()) {
                    this.playerData.put(player.getUniqueId(), new PlayerInfo(allRecipes, 0));
                    openRecipeGUI(player, allRecipes, null);
                }
            } else {
                event.getWhoClicked().sendRichMessage("<red>Keine Rezepte für dieses Item gefunden!");
            }
        }
    }

    private void openRecipeGUI(Player player, List<List<ItemStack>> recipes, @Nullable Integer page) {
        Inventory inv = guiManager.createGUI("recipe_selection", "Select Recipe", 54);
        List<Integer> recipeOne = List.of(10, 11, 12, 19, 20, 21, 28, 29, 30);
        List<Integer> recipeTwo = List.of(14, 15, 16, 23, 24, 25, 32, 33, 34);
        List<Integer> singleRecipeSlots = List.of(12, 13, 14, 21, 22, 23, 30, 31, 32);
        List<Integer> glasslots = new ArrayList<>();

        if (page == null) page = 0;
        this.playerData.put(player.getUniqueId(), new PlayerInfo(recipes, page));
        boolean isSingleRecipePage = (recipes.size() == page * 2 + 1);

        for(int i = 0; i< 54; i++) {
            if (isSingleRecipePage) {
                if (!singleRecipeSlots.contains(i) && i != 40) {
                    glasslots.add(i);
                }
            }
            else {
                if (!recipeOne.contains(i) && !recipeTwo.contains(i) && i != 38 && i != 42) {
                    glasslots.add(i);
                }
            }
        }

        if (isSingleRecipePage) {
            List<ItemStack> currentRecipe = recipes.get(page * 2);
            for (int i = 0; i < singleRecipeSlots.size(); i++) {
                if (i < currentRecipe.size()) {
                    ItemStack item = currentRecipe.get(i);
                    guiManager.addItem(inv, item, singleRecipeSlots.get(i), this::cancleClick);
                }
            }
            ItemStack selectPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            selectPane.editMeta(meta -> meta.displayName(Component.text("Select this Recipe", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)));
            guiManager.addItem(inv, selectPane, 40, this::cancleClick);
        } else {
            if (recipes.size() > page * 2) {
                List<ItemStack> leftRecipe = recipes.get(page * 2);
                for (int pos : recipeOne) {
                    int index = recipeOne.indexOf(pos);
                    if (index < leftRecipe.size()) {
                        ItemStack item = leftRecipe.get(index);
                        guiManager.addItem(inv, item, pos, this::cancleClick);
                    }
                }
            }
            if (recipes.size() > (page * 2 + 1)) {
                List<ItemStack> rightRecipe = recipes.get(page * 2 + 1);
                for (int pos : recipeTwo) {
                    int index = recipeTwo.indexOf(pos);
                    if (index < rightRecipe.size()) {
                        ItemStack item = rightRecipe.get(index);
                        guiManager.addItem(inv, item, pos, this::cancleClick);
                    }
                }
            }
            ItemStack selectPane1 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            selectPane1.editMeta(meta -> meta.displayName(Component.text("Select this Recipe", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)));
            guiManager.addItem(inv, selectPane1, 38, this::cancleClick);
            ItemStack selectPane2 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            selectPane2.editMeta(meta -> meta.displayName(Component.text("Select this Recipe", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)));
            guiManager.addItem(inv, selectPane2, 42, this::cancleClick);
        }

        for (int i : glasslots) {
            if (i == 45 && page > 0) {
                ItemStack pageItem = new ItemStack(Material.PAPER);
                pageItem.editMeta(meta -> meta.displayName(Component.text("Vorherige Seite")));
                guiManager.addItem(inv, pageItem, i, this::previousPage);
            } else if (i == 53 && recipes.size() > ((page + 1) * 2)) {
                ItemStack pageItem = new ItemStack(Material.PAPER);
                pageItem.editMeta(meta -> meta.displayName(Component.text("Nächste Seite")));
                guiManager.addItem(inv, pageItem, i, this::nextPage);
            } else {
                ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                item.editMeta(meta -> meta.displayName(Component.empty()));
                guiManager.addItem(inv, item, i, this::cancleClick);
            }
        }

        player.openInventory(inv);
    }



    private void loadPage(Player player, int page) {
        PlayerInfo info = playerData.get(player.getUniqueId());
        List<List<ItemStack>> itemstacks = info.itemStacks;
        openRecipeGUI(player, itemstacks, page);
    }


    private void nextPage(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        loadPage(player, playerData.get(player.getUniqueId()).page + 1);
    }

    private void previousPage(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        loadPage(player, playerData.get(player.getUniqueId()).page - 1);
    }

    private void removeItem(InventoryClickEvent event) {
        event.setCancelled(true);
        Inventory topinv = event.getView().getTopInventory();
        topinv.remove(Objects.requireNonNull(event.getCurrentItem()));
        topinv = rearrangeInventory(topinv);
    }

    private void cancleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public Inventory rearrangeInventory(Inventory inv) {
        List<ItemStack> items = new ArrayList<>(Arrays.asList(inv.getContents()));
        inv.clear();
        for (ItemStack item : items) {
            if (item != null) inv.addItem(item);
        }
        return inv;
    }

    public static List<List<ItemStack>> generateAllCombinations(List<List<ItemStack>> slotOptions) {
        List<List<ItemStack>> results = new ArrayList<>();
        int maxOptions = 1;
        for (List<ItemStack> slot : slotOptions) {
            maxOptions = Math.max(maxOptions, slot.size());
        }
        for (int i = 0; i < maxOptions; i++) {
            List<ItemStack> recipe = new ArrayList<>();
            for (List<ItemStack> slot : slotOptions) {
                if (slot.size() == 1) {
                    recipe.add(slot.get(0));
                } else if (i < slot.size()) {
                    recipe.add(slot.get(i));
                } else {
                    recipe.add(slot.get(0));
                }
            }
            results.add(recipe);
        }
        return results;
    }


    public static class PlayerInfo {
        @Getter
        private List<List<ItemStack>> itemStacks;
        @Getter
        private int page;

        public PlayerInfo(List<List<ItemStack>> itemStacks, int page) {
            this.itemStacks = itemStacks;
            this.page = page;
        }
    }


}
