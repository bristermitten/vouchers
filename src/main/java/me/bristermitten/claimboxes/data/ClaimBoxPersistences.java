package me.bristermitten.claimboxes.data;

import me.bristermitten.claimboxes.ClaimBoxes;
import me.bristermitten.mittenlib.persistence.Persistence;
import me.bristermitten.mittenlib.persistence.Persistences;

import java.util.UUID;

public interface ClaimBoxPersistence extends Persistence<UUID, ClaimBox> {
}
