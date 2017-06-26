--
-- hpc_notification.sql
--
-- Copyright SVG, Inc.
-- Copyright Leidos Biomedical Research, Inc
-- 
-- Distributed under the OSI-approved BSD 3-Clause License.
-- See http://ncip.github.com/HPC/LICENSE.txt for details.
--
--
-- @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
-- @version $Id$
--

DROP TABLE IF EXISTS public."HPC_NOTIFICATION_SUBSCRIPTION";
CREATE TABLE public."HPC_NOTIFICATION_SUBSCRIPTION"
(
  "ID" SERIAL PRIMARY KEY,
  "USER_ID" text NOT NULL,
  "EVENT_TYPE" text NOT NULL,
  "NOTIFICATION_DELIVERY_METHODS" text[] NOT NULL,
  CONSTRAINT "HPC_NOTIFICATION_SUBSCRIPTION_unique" UNIQUE ("USER_ID", "EVENT_TYPE")
)
WITH (
  OIDS=TRUE
);
ALTER TABLE public."HPC_NOTIFICATION_SUBSCRIPTION"
  OWNER TO postgres;
  
DROP TABLE IF EXISTS public."HPC_NOTIFICATION_TRIGGER";
CREATE TABLE public."HPC_NOTIFICATION_TRIGGER"
(
  "NOTIFICATION_SUBSCRIPTION_ID" integer REFERENCES public."HPC_NOTIFICATION_SUBSCRIPTION"("ID") ON DELETE CASCADE ON UPDATE CASCADE,
  "NOTIFICATION_TRIGGER" text[]
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."HPC_NOTIFICATION_TRIGGER"
  OWNER TO postgres;  
  
DROP TABLE IF EXISTS public."HPC_EVENT";
CREATE TABLE public."HPC_EVENT"
(
  "ID" SERIAL PRIMARY KEY,
  "USER_IDS" text NOT NULL,
  "TYPE" text NOT NULL,
  "PAYLOAD" bytea,
  "CREATED" timestamp NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."HPC_EVENT"
  OWNER TO postgres;
  
DROP TABLE IF EXISTS public."HPC_EVENT_HISTORY";
CREATE TABLE public."HPC_EVENT_HISTORY"
(
  "ID" integer PRIMARY KEY,
  "USER_IDS" text NOT NULL,
  "TYPE" text NOT NULL,
  "PAYLOAD" bytea,
  "CREATED" timestamp NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."HPC_EVENT_HISTORY"
  OWNER TO postgres;
  
DROP TABLE IF EXISTS public."HPC_NOTIFICATION_DELIVERY_RECEIPT";
CREATE TABLE public."HPC_NOTIFICATION_DELIVERY_RECEIPT"
(
  "EVENT_ID" integer NOT NULL,
  "USER_ID" text NOT NULL,
  "NOTIFICATION_DELIVERY_METHOD" text NOT NULL,
  "DELIVERY_STATUS" boolean NOT NULL,
  "DELIVERED" timestamp NOT NULL,
  CONSTRAINT "HPC_NOTIFICATION_DELIVERY_RECEIPT_pkey" PRIMARY KEY ("EVENT_ID", "USER_ID", "NOTIFICATION_DELIVERY_METHOD")
)
WITH (
  OIDS=FALSE
);