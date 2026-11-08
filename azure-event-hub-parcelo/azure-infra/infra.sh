#!/usr/bin/env bash
set -euo pipefail

LOCATION="westeurope"
RESOURCE_GROUP="rg-parcelo-lab"
# Namespace names are global (they become a hostname), so add a suffix
# derived from your subscription: unique, but the same on every run.
SUFFIX=$(az account show --query id -o tsv | cut -c1-6)
NAMESPACE="parcelo-lab-${SUFFIX}"

echo "Creating resource group ${RESOURCE_GROUP} in ${LOCATION}"
az group create --name "$RESOURCE_GROUP" --location "$LOCATION" -o none

echo "Creating Event Hubs namespace ${NAMESPACE} (Standard, 1 TU)"
az eventhubs namespace create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$NAMESPACE" \
  --location "$LOCATION" \
  --sku Standard \
  --capacity 1 \
  -o none

HUB="parcels"

echo "Creating hub ${HUB} (4 partitions, 1-day retention)"
az eventhubs eventhub create \
  --resource-group "$RESOURCE_GROUP" \
  --namespace-name "$NAMESPACE" \
  --name "$HUB" \
  --partition-count 4 \
  --retention-time-in-hours 24 \
  --cleanup-policy Delete \
  -o none

for CG in tracking billing; do
  echo "Creating consumer group ${CG}"
  az eventhubs eventhub consumer-group create \
    --resource-group "$RESOURCE_GROUP" \
    --namespace-name "$NAMESPACE" \
    --eventhub-name "$HUB" \
    --name "$CG" \
    -o none
done

echo "Creating SAS policies on ${HUB}: scanner-send (Send), reader-listen (Listen)"
az eventhubs eventhub authorization-rule create \
  --resource-group "$RESOURCE_GROUP" \
  --namespace-name "$NAMESPACE" \
  --eventhub-name "$HUB" \
  --name scanner-send \
  --rights Send \
  -o none

az eventhubs eventhub authorization-rule create \
  --resource-group "$RESOURCE_GROUP" \
  --namespace-name "$NAMESPACE" \
  --eventhub-name "$HUB" \
  --name reader-listen \
  --rights Listen \
  -o none

echo "Done: ${NAMESPACE}.servicebus.windows.net"
